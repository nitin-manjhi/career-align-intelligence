This guide will walk you through the process of deploying the ResumeAi and career-align-intelligence suite to your local Kubernetes cluster.

## 🚀 Minikube Quick Start

If you are using Minikube on Windows, follow these exact steps:

1. **Start Minikube**: `minikube start`
2. **Point Docker to Minikube**: `minikube docker-env | Invoke-Expression`
3. **Build Images** (from the project root):
   ```powershell
   ./mvnw clean package -DskipTests -f ./career-align-intelligence/pom.xml
   docker build -t career-backend:latest ./career-align-intelligence
   docker build -t resume-frontend:latest ./ResumeAi
   docker build -t linkedin-scraper-api:latest ./jobScrapper
   ```
4. **Deploy**:
   ```powershell
   cd k8s-deploy  # Navigate to wherever you moved the folder
   ./apply-all.ps1
   ```
5. **Access**: `minikube service frontend -n resume-app`

## Prerequisites

- **Kubernetes Cluster**: Docker Desktop (with Kubernetes enabled), Minikube, or Kind.
- **kubectl**: Installed and configured to point to your local cluster.
- **Maven**: To build the backend JAR.
- **Node.js**: To build the frontend and scraper (if building images locally).

## Step 1: Build Application Artifacts

### Backend
Navigate to the `career-align-intelligence` directory and build the JAR:
```powershell
./mvnw clean package -DskipTests
```

### Frontend
Navigate to the `ResumeAi` directory and build the production assets:
```powershell
npm install
npm run build -- --configuration production
```

## Step 2: Build Docker Images

You need to build the Docker images so they are available to your Kubernetes cluster.

### Backend
```powershell
docker build -t career-backend:latest ./career-align-intelligence
```

### Frontend
```powershell
docker build -t resume-frontend:latest ./ResumeAi
```

### Scraper
```powershell
docker build -t linkedin-scraper-api:latest ./jobScrapper
```

> [!NOTE]
> If you are using **Minikube**, run `minikube docker-env | Invoke-Expression` before building images to build them directly inside the Minikube Docker daemon.
> If using **Kind**, run `kind load docker-image <image-name>:latest`.

## Step 3: Apply Kubernetes Manifests

Navigate to the `kubernetes` directory (wherever you moved it) and apply the manifests in order:

```powershell
kubectl apply -f 00-namespace.yaml
kubectl apply -f 01-postgres.yaml
kubectl apply -f 02-redis.yaml
kubectl apply -f 03-kafka.yaml
kubectl apply -f 04-config.yaml
kubectl apply -f 05-scraper.yaml
kubectl apply -f 06-backend.yaml
kubectl apply -f 07-frontend.yaml
```

## Step 4: Verify Deployment

Check if all pods are running:
```powershell
kubectl get pods -n resume-app
```

Verify services:
```powershell
kubectl get svc -n resume-app
```

## Step 5: Access the Application

The frontend service is configured as a `LoadBalancer`. 
- On **Docker Desktop**, you can access it at `http://localhost`.
- On **Minikube**, you may need to run `minikube service frontend -n resume-app`.
