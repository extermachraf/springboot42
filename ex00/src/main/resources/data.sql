-- Test users for Exercise 00
-- Password "password123" encoded with BCrypt (strength 10)

DELETE FROM users WHERE username IN ('admin', 'user');

INSERT INTO users (username, password, email, first_name, last_name, role, status, created_at, updated_at)
VALUES
    ('admin', '$2a$10$MgE/Gix6fj.7NvC0.s8o7uq9X9wU.6GUa.JSzxzcouDPsMmAbul0G', 'admin@cinema.com', 'Admin', 'User', 'ADMIN', 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('user', '$2a$10$MgE/Gix6fj.7NvC0.s8o7uq9X9wU.6GUa.JSzxzcouDPsMmAbul0G', 'user@cinema.com', 'Test', 'User', 'USER', 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);