````markdown

### Build the Docker Image

You can build the image either **locally (Mac/Linux)** or directly on a **remote server (e.g., AWS EC2 Ubuntu)**.

### Build on Local Machine (Mac)

```bash
# Navigate to the project directory
cd 2025_LIUYITING_CoinChangeService

# Build the Docker image using the Dockerfile
docker build -t coin-change-service .
````

Once built, you can:

Run it locally:

```bash
docker run -d -p 8080:8080 coin-change-service
```
Or export it to a `.tar` file to transfer to a server:

```bash
docker save coin-change-service > coin-change-service.tar
```
---
###  Run on Remote Server (e.g., AWS EC2)

#### Load from a local `.tar` image

```bash
# On local machine, send the image to server
scp -i your-key.pem coin-change-service.tar ubuntu@<your-public-ip>:~/

# On the server, load the image
ssh -i your-key.pem ubuntu@<your-public-ip>
docker load < coin-change-service.tar
```
###  Run the Container

```bash
docker run -d --restart always -p 8080:8080 coin-change-service
```

##  Project Structure

```
CoinChangeService/
├── src/
│   ├── main/java/...      # Source code
├── config.yml             # Dropwizard configuration
├── pom.xml                # Maven build config
├── Dockerfile             # Docker multi-stage build
└── README.md              # Project documentation
```
---

## 🐘 Tech Stack

* Java 17
* Dropwizard
* Maven
* Docker
* JUnit
