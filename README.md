# ENSET Guide : Assistant Intelligent Basé sur RAG

## 🎓 Présentation du Projet

ENSET Guide est une application innovante d'intelligence artificielle conçue spécifiquement pour les étudiants de l'ENSET Mohammedia. Cette solution de chat intelligente utilise la technologie RAG (Retrieval-Augmented Generation) pour fournir des réponses précises et contextuelles basées sur les documents et ressources de l'école.
## Démonstration en vidéo
Regardez la vidéo de démonstration : [video.mp4](Demo/ENSETGUIDE.mp4)
## 🚀 Fonctionnalités Principales

### 1. Chat Intelligent
- Interface conversationnelle conviviale
- Réponses générées dynamiquement en fonction du contexte
- Support de multiple formats de documents (PDF, DOCX, TXT)

### 2. Gestion des Conversations
- Création et gestion de plusieurs conversations
- Historique des conversations sauvegardé
- Possibilité de renommer et supprimer des conversations

### 3. Fonctionnalités Avancées
- Téléchargement et indexation de documents
- Recherche sémantique dans les documents
- Reconnaissance vocale intégrée
- Génération de réponses contextuelles

## 🛠 Technologies Utilisées

### Backend
- **Langage**: Java
- **Framework UI**: JavaFX
- **Base de Données**: PostgreSQL
- **Modèle IA**: RAG (Retrieval-Augmented Generation)

### Librairies et Dépendances
- JDBC pour la connexion à PostgreSQL
- Apache PDFBox pour le traitement PDF
- JavaFX pour l'interface utilisateur
- Vosk pour la reconnaissance vocale

## 📦 Architecture Système

### Composants Principaux
1. **Interface Utilisateur (SimpleChatInterface)**
    - Gestion de l'interface graphique
    - Interaction avec l'utilisateur

2. **Gestionnaire de Base de Données (ChatDatabaseManager)**
    - Connexion et interactions avec PostgreSQL
    - Sauvegarde des conversations
    - Gestion des documents

3. **Modèle RAG (Rag)**
    - Indexation des documents
    - Génération de réponses contextuelles
    - Recherche sémantique

## 🔧 Configuration Requise

### Prérequis
- Java JDK 11 ou supérieur
- PostgreSQL 12 ou supérieur
- Connexion Internet pour les requêtes RAG
- API Key pour LLM

### Dépendances
- Ajouter les JAR suivants au classpath :
    - postgresql-jdbc-driver
    - javafx-controls
    - apache-pdfbox

## 💾 Utilisation
### Lancez l'application :

- L'interface principale s'ouvre avec une liste de conversations et une zone de chat.

### Créez une nouvelle conversation :

- Cliquez sur "New Chat" pour démarrer une nouvelle conversation.

### Chargez un fichier :

- Utilisez le bouton "Load File" pour charger un fichier (PDF, image, texte, Word).

- Le texte extrait sera utilisé pour enrichir les réponses du chatbot.

### Posez des questions :

- Tapez ou dictez vos questions dans la zone de texte.

- Le chatbot répondra en utilisant le modèle RAG et les données extraites des fichiers.

### Gérez les conversations :

- Renommez ou supprimez des conversations via le menu contextuel.



## 🚀 Démarrage Rapide

1. Cloner le dépôt
```bash
git clone https://github.com/votre-username/enset-guide.git
```

2. Configurer la base de données
- Créer une base de données PostgreSQL
- Mettre à jour les paramètres de connexion dans `DatabaseConfig.java`

3. Compiler et exécuter
```bash
javac SimpleChatInterface.java
java SimpleChatInterface
```

## 🤝 Contribution

Les contributions sont les bienvenues ! Veuillez suivre ces étapes :
1. Fork du projet
2. Créer une branche de fonctionnalité
3. Commit de vos modifications
4. Push et créer une Pull Request





---

**Note**: Ce projet est en constant développement. Les fonctionnalités et l'architecture peuvent évoluer.