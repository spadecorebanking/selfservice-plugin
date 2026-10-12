# Plugin JAR deployment

`maven-build.yml` keeps the JAR produced by integration tests as the
`selfservice-plugin-jar` GitHub Actions artifact. After unit tests, integration
tests, and the coverage gate pass, pushes to `develop` upload that same JAR to
the configured server using SCP. Pull requests never deploy.

Configure these in the **mobile-app repository**, under Settings → Secrets and
variables → Actions:

| Kind | Name | Value |
| --- | --- | --- |
| Secret | `SSH_HOST` | Server hostname or IPv4 address (including a Hostinger VPS) |
| Secret | `SSH_USER` | SSH account with write access to the destination |
| Secret | `SSH_PASSWORD` | Password for the SSH account |
| Secret | `SSH_KNOWN_HOSTS` | Verified OpenSSH known_hosts entry for the server |
| Variable | `SSH_PORT` | SSH port; defaults to `22` |
| Variable | `JAR_DEPLOY_PATH` | Absolute destination directory, e.g. `/opt/fineract/plugins/libs` |

Enable SSH password authentication for the deployment account on the server.
The workflow uses `sshpass` with the password supplied through an environment
variable; the password is not passed as a command-line argument.
Obtain the server host key through a trusted channel and verify its fingerprint
before saving `SSH_KNOWN_HOSTS`. For nonstandard ports, the known_hosts entry
must use `[hostname]:port`. Host key checking is enforced.

The workflow creates the destination folder if needed. Paths must contain only
letters, digits, underscores, dots, slashes, and hyphens. Uploads preserve the
Maven JAR filename and overwrite an existing file with the same name. Older
versions are not removed; use a staging folder if your runtime automatically
loads every JAR. No application restart is performed.

The website's `hostinger-deploy.yml` deploys Docker Compose projects through the
Hostinger API. This workflow uses SSH/SCP instead because the deliverable is a
JAR copied to a server folder.

Automatic JFrog publishing is temporarily disabled. `jfrog-publish.yml` remains
available through **Run workflow** for deliberate manual publishing.
