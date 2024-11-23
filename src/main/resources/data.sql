INSERT IGNORE INTO roles (roleId, name) VALUES (1, 'ADMIN');
INSERT IGNORE INTO roles (roleId, name) VALUES (2, 'USER');

INSERT IGNORE INTO payments (paymentId, paymentMethod) VALUES (1, 'CASH');
INSERT IGNORE INTO payments (paymentId, paymentMethod) VALUES (2, 'CREDIT_CARD');
INSERT IGNORE INTO payments (paymentId, paymentMethod) VALUES (3, 'PAYPAL');

INSERT IGNORE INTO users (userId, email, firstName, lastName, mobileNumber , password, roleId) VALUES (1, 'admin@gmail.com', 'Admin', 'Admin', '0909090909', '$2a$10$mQK7WC7yP9JWzNISPhQEt.Sg48pvPnK/muaB49UtCu7SZmm.1lRcm', 1);

