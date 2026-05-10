# Setup

Acest proiect a fost rulat pe Arch Linux. Mai jos gasesti si pasii pentru Ubuntu si Windows.

## Cerinte
- podman (container Oracle XE)
- Java 21 (pentru Spring Boot)
- Node.js + npm (pentru frontend)

## Instalare (Arch Linux)
```bash
sudo pacman -Syu podman jdk-openjdk nodejs npm
```

## Instalare (Ubuntu)
```bash
sudo apt update
sudo apt install -y podman openjdk-21-jdk nodejs npm
```

## Instalare (Windows)
- Instaleaza Java 21 (Temurin/OpenJDK)
- Instaleaza Node.js LTS (include npm)
- Instaleaza Podman Desktop

## Rulare
```bash
cd /home/cata884/Downloads/db_project
bash ./setup.sh
```

## Rulare (Windows)
```bash
cd C:\\path\\to\\db_project
bash ./setup.sh
```

## Ce face `setup.sh`
- porneste containerul Oracle XE
- asteapta ca DB sa fie gata
- ruleaza scripturile SQL din `Project/database/`
- porneste backend-ul in background
- porneste frontend-ul cu `npm run dev`

Note:
- `setup.sh` foloseste `sudo` pentru Podman (pe Linux).
- Daca vrei sa opresti backend-ul, foloseste PID-ul afisat de script.
- Pentru a reinitializa baza, ruleaza din `Project/database/`:

```bash
sh ./script.sh drop
sh ./script.sh create
```
