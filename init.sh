sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie

curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.38.0/install.sh | bash
. ~/.nvm/nvm.sh
nvm install 14.19.0 && nvm use 14.19.0
export NODE_OPTIONS=--openssl-legacy-provider

cd infra
docker compose up
