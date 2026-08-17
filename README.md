# OBDiary

Aplicação Android para registro e gestão de diários, desenvolvida para proporcionar a organização de anotações pessoais em dispositivos móveis.

## Demonstração

<video controls src="https://github.com/user-attachments/assets/84442704-1398-400a-9226-2599e5182f00"></video>

## Funcionalidades

- Registro de entradas de diário.
- Gestão e organização de anotações.
- Interface intuitiva para usuários Android.

## Estrutura do Projeto

O projeto é organizado seguindo princípios de modularização para separação de responsabilidades:

```text
OBDiary/
├── app/                    # Ponto de entrada da aplicação e injeção de dependências
├── core/
│   └── datasource/         # Camada de persistência e acesso a dados (repositórios e APIs)
└── feature/                 # Módulos de funcionalidades específicas da interface e lógica de negócio
```
# Tecnologias
- Linguagem: Kotlin / Java
- Framework: Android SDK
- Build Tool: Gradle
# Instalação e Execução
1. Clone o repositório:
```text
git clone https://github.com/rafaelmelom-dev/OBDiary.git
cd OBDiary
```
3. Abra o projeto no Android Studio.
4. Sincronize os arquivos do Gradle e execute a aplicação em um emulador ou dispositivo Android.
