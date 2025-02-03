package com.enset.test;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ChatDatabaseManager {
    private final connexion dbConnection;
    private static final Logger logger = Logger.getLogger(ChatDatabaseManager.class.getName());
    public ChatDatabaseManager() {
        this.dbConnection = new connexion();
        createTables();
    }

    private void createTables() {
        String createConversationsTable = """
        CREATE TABLE IF NOT EXISTS conversations (
            id SERIAL PRIMARY KEY,
            title VARCHAR(255) NOT NULL,
            context TEXT,  -- Ajoutez cette colonne
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
    """;

        String createMessagesTable = """
        CREATE TABLE IF NOT EXISTS messages (
            id SERIAL PRIMARY KEY,
            conversation_id INTEGER NOT NULL,
            message_text TEXT NOT NULL,
            is_user BOOLEAN NOT NULL,
            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
        )
    """;

        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createConversationsTable);
            stmt.execute(createMessagesTable);
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int createNewConversation(String title) {
        String sql = "INSERT INTO conversations (title) VALUES (?) RETURNING id";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Created new conversation with ID: " + id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating conversation: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public int createNewConversationWithMessage(String title, String initialMessage) {
        String sqlConversation = "INSERT INTO conversations (title) VALUES (?) RETURNING id";
        String sqlMessage = "INSERT INTO messages (conversation_id, message_text, is_user) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.connect()) {
            conn.setAutoCommit(false); // Désactive le mode auto-commit

            try (PreparedStatement pstmtConversation = conn.prepareStatement(sqlConversation);
                 PreparedStatement pstmtMessage = conn.prepareStatement(sqlMessage)) {

                // Crée une nouvelle conversation
                pstmtConversation.setString(1, title);
                ResultSet rs = pstmtConversation.executeQuery();

                if (rs.next()) {
                    int conversationId = rs.getInt(1);

                    // Ajoute le message initial
                    pstmtMessage.setInt(1, conversationId);
                    pstmtMessage.setString(2, initialMessage);
                    pstmtMessage.setBoolean(3, true); // Supposons que le message est de l'utilisateur
                    pstmtMessage.executeUpdate();

                    conn.commit(); // Valide la transaction
                    return conversationId;
                } else {
                    conn.rollback(); // Annule la transaction en cas d'échec
                    return -1;
                }
            } catch (SQLException e) {
                conn.rollback(); // Annule la transaction en cas d'erreur
                logger.severe("Error creating conversation with message: " + e.getMessage());
                return -1;
            }
        } catch (SQLException e) {
            logger.severe("Error connecting to database: " + e.getMessage());
            return -1;
        }
    }

    public boolean saveMessage(int conversationId, String message, boolean isUser) {
        if (message == null || message.trim().isEmpty()) {
            logger.warning("Cannot save an empty message.");
            return false;
        }

        if (conversationId <= 0) {
            logger.warning("Invalid conversation ID: " + conversationId);
            return false;
        }

        String sql = "INSERT INTO messages (conversation_id, message_text, is_user) VALUES (?, ?, ?)";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            pstmt.setString(2, message);
            pstmt.setBoolean(3, isUser);

            int affectedRows = pstmt.executeUpdate();
            logger.info("Message saved. Affected rows: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.severe("Error saving message: " + e.getMessage());
            return false;
        }
    }

    public List<Message> getConversationMessages(int conversationId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT message_text, is_user, timestamp FROM messages WHERE conversation_id = ? ORDER BY timestamp";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message(
                            rs.getString("message_text"),
                            rs.getBoolean("is_user"),
                            rs.getTimestamp("timestamp")
                    );
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving messages: " + e.getMessage());
        }

        return messages;
    }
    public List<Conversation> getAllConversations() {
        List<Conversation> conversations = new ArrayList<>();
        String sql = "SELECT id, title, created_at FROM conversations ORDER BY created_at DESC";

        try (Connection conn = dbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Conversation conversation = new Conversation(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("created_at")
                );
                conversations.add(conversation);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving conversations: " + e.getMessage());
            e.printStackTrace();
        }

        return conversations;
    }

    public boolean deleteConversation(int conversationId) {
        String sql = "DELETE FROM conversations WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            int affectedRows = pstmt.executeUpdate();
            clearDatabase(conversationId);
            System.out.println("Deleted conversation. Affected rows: " + affectedRows);
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting conversation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    //Mettre à jour le contexte à chaque interaction
    public void updateConversationContext(int conversationId, String newContext) {
        String sql = "UPDATE conversations SET context = ? WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContext);
            pstmt.setInt(2, conversationId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Context updated. Affected rows: " + affectedRows);

        } catch (SQLException e) {
            System.err.println("Error updating context: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //récupérer le contexte
    public String getConversationContext(int conversationId) {
        String sql = "SELECT context FROM conversations WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("context");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving context: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void updateConversationTitle(int conversationId, String newTitle) {
        String sql = "UPDATE conversations SET title = ? WHERE id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTitle);
            pstmt.setInt(2, conversationId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("Conversation title updated. Affected rows: " + affectedRows);

        } catch (SQLException e) {
            System.err.println("Error updating conversation title: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearConversationHistory(int conversationId) {
        String sql = "DELETE FROM messages WHERE conversation_id = ?";

        try (Connection conn = dbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, conversationId);
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Cleared conversation history. Affected rows: " + affectedRows);

        } catch (SQLException e) {
            System.err.println("Error clearing conversation history: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void clearDatabase(int conversationId) {
        connexion conn = new connexion();
        Connection dbConnection = conn.connect();
        String deleteSQL = "DELETE FROM items WHERE conversation_id = ?"; // Supprimer uniquement les données de la conversation spécifique

        try (PreparedStatement preparedStatement = dbConnection.prepareStatement(deleteSQL)) {
            preparedStatement.setInt(1, conversationId); // Utiliser l'identifiant de conversation
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println("Database cleared successfully. Rows affected: " + rowsAffected);
            clearConversationHistory(conversationId); // Effacer l'historique de conversation
        } catch (SQLException e) {
            System.err.println("Error clearing database: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (dbConnection != null) {
                    dbConnection.close(); // Fermer la connexion après utilisation
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}

