# iPhoneFixit EC2 CI/CD setup

Both repositories deploy independently to the same Ubuntu x86_64 EC2 host.
Backend uses Java 21 and Aiven MySQL. Frontend uses Node 24 to build React,
then Nginx serves the build and proxies /api and /uploads to the backend.
This first setup serves HTTP on port 80; configure a domain and HTTPS before
using real admin credentials or customer data over the public internet.

## 1. Prepare EC2 once

Install Docker Engine and the Compose plugin using Docker's Ubuntu apt instructions:
https://docs.docker.com/engine/install/ubuntu/#install-using-the-apt-repository
Compose 2.30+ is required for the raw environment file format.
Use an amd64 instance: these workflows build linux/amd64 images.

As the Ubuntu SSH user, run:

```bash
sudo usermod -aG docker "$USER"
sudo install -d -m 750 -o "$USER" -g "$USER" /opt/iphonefixit
```

Log out and reconnect, then run:

```bash
docker version
docker compose version
docker network create iphonefixit
mkdir -p /opt/iphonefixit/backend /opt/iphonefixit/frontend
```

If the network already exists, keep it. Docker access grants powerful host access;
use a dedicated deployment key. The frontend needs port 80 free. If host Nginx
already uses it, resolve that port conflict before enabling deployment.

Security group: allow HTTP 80 for the initial check, HTTPS 443 when configured,
and SSH 22 from your admin IP and the CI runner's source network. Standard GitHub
hosted runners do not use your laptop's IP: an SSH rule restricted only to your
IP will block the workflow. Use a runner with static egress and allow its IP,
or manage GitHub Actions runner CIDRs. Do not open 8080 or 3306 publicly.
Allow outbound access to Docker Hub and Aiven's actual database port. If Aiven
has an IP allowlist, include the EC2 outbound public IP (or NAT IP).

## 2. Set backend credentials on EC2

Copy deploy/backend.env.example to /opt/iphonefixit/backend.env and edit it:

```bash
chmod 600 /opt/iphonefixit/backend.env
nano /opt/iphonefixit/backend.env
openssl rand -hex 32
```

Use the random output as JWT_SECRET. Replace every placeholder, using the actual
Aiven database name and port. FRONTEND_URL and BACKEND_URL must both be your
public origin, for example http://203.0.113.10, without a trailing slash.
The raw env file preserves dollar signs in passwords: do not quote the values.
DB_SSL_MODE=REQUIRED encrypts the Aiven connection; server certificate validation
requires configuring Aiven's CA trust and VERIFY_IDENTITY separately.
Never commit the filled environment file. Existing local/Aiven data is not migrated
by these pipelines. Pointing to an existing database preserves its records; back it
up first because the application's existing ddl-auto=update can change the schema.
Initial admin settings only create an account on first startup; they do not reset
the password of an account already in the database.

## 3. Configure both GitHub repositories

Settings > Secrets and variables > Actions > Repository secrets:

| Secret | Value |
| --- | --- |
| DOCKERHUB_USERNAME | Your Docker Hub username |
| DOCKERHUB_TOKEN | Docker Hub token with push access |
| EC2_HOST | EC2 public IP or DNS name, without http:// |
| EC2_USER | ubuntu |
| EC2_SSH_KEY | Full OpenSSH/PEM private deployment key, not a .ppk file |
| EC2_KNOWN_HOSTS | Verified SSH known_hosts entry for EC2_HOST |

Obtain the server's host public key through a trusted EC2 console/SSH session:

```bash
cat /etc/ssh/ssh_host_ed25519_key.pub
```

EC2_KNOWN_HOSTS is one line: EC2_HOST followed by a space, then the key type
and base64 key above. Omit the comment. If the instance is replaced, verify its
new host key and update this secret. Never disable SSH host key checking.

Create Docker Hub repositories iphonefixit-backend and iphonefixit-frontend.
For private images, run docker login on EC2 as the same deployment user with
a pull-capable token. The workflow's registry login applies only to the CI runner.

Create a GitHub environment named production in both repositories, optionally
with required reviewers. Add repository variables:

| Variable | Repositories | Value |
| --- | --- | --- |
| PUBLIC_URL | Frontend | Same public origin as FRONTEND_URL, no trailing slash |
| ENABLE_EC2_DEPLOY | Both | Leave false until server setup is complete |

PUBLIC_URL is embedded in frontend image/bill links at build time. Changing the
IP/domain requires updating this variable and backend.env and rerunning both
pipelines. Use a stable address or domain. Do not put credentials in VITE variables.

## 4. First deployment and subsequent updates

Review and merge both PRs after their CI succeeds. Main-branch builds push images
tagged with their commit SHA; pull requests only test/build and never deploy.
Set ENABLE_EC2_DEPLOY=true in the backend repository and run Actions > Build and
deploy backend > Run workflow on main. After it succeeds, enable and run frontend.
Every later main push deploys the corresponding component automatically.

The backend's existing context test runs against disposable MySQL in CI. The
frontend has no test script; its CI checks the production Docker build.

Open the public URL, then /admin/login. Verify login, inventory, repair tracking,
image upload, and bill download. The frontend health probe checks Nginx; it does
not replace these end-to-end checks. Configure TLS (for example with an AWS load
balancer or an HTTPS reverse proxy) and update both public URL settings before
real use. No AWS resources or HTTPS certificates are created by these files.

## Operations

Backend images persist in the iphonefixit-uploads Docker volume across container
updates. Back up that volume and Aiven regularly; do not use compose down -v.
For logs use the image from last-successful-image:

```bash
cd /opt/iphonefixit/backend
export IMAGE=$(cat last-successful-image)
docker compose logs --tail=100 backend
```

Deployments wait for container health. On failure they attempt to restore the
last successful image and mark the workflow failed. First deployments have no
previous image. Image rollback does not reverse database/schema changes.
To redeploy a previously successful SHA tag manually:

```bash
bash /opt/iphonefixit/backend/deploy.sh YOUR_DOCKERHUB_USER/iphonefixit-backend:COMMIT_SHA
```

Frontend has the same command in /opt/iphonefixit/frontend. Both share a host
lock, preventing simultaneous deployments. There can be a short interruption
during container replacement. Old Docker images are retained for rollback;
monitor disk space and remove only images you no longer need.
