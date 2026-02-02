-- Insert default admin user (password: Admin@123)
INSERT INTO users (username, email, password, role, active) VALUES
('admin@swift.com', 'admin@swift.com',
 '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYJyxjJXu.i',
 'ADMIN', true);

-- Insert default operations user (password: Ops@123)
INSERT INTO users (username, email, password, role, active) VALUES
('ops@swift.com', 'ops@swift.com',
 '$2a$12$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi',
 'OPERATIONS', true);

-- Insert sample vehicles
INSERT INTO vehicles (registration_number, make, model, year, active) VALUES
('GH-1234-20', 'Toyota', 'Hiace', 2020, true),
('GH-5678-21', 'Mercedes', 'Sprinter', 2021, true),
('GH-9012-19', 'Ford', 'Transit', 2019, true),
('GH-3456-22', 'Toyota', 'Coaster', 2022, true),
('GH-7890-20', 'Hyundai', 'H350', 2020, true);

-- Insert sample drivers
INSERT INTO drivers (full_name, phone_number, license_number, status, deleted) VALUES
('Kwame Mensah', '+233244111111', 'DL001234567', 'ACTIVE', false),
('Akua Osei', '+233244222222', 'DL002345678', 'ACTIVE', false),
('Kofi Adu', '+233244333333', 'DL003456789', 'SUSPENDED', false),
('Abena Owusu', '+233244444444', 'DL004567890', 'ACTIVE', false),
('Yaw Boateng', '+233244555555', 'DL005678901', 'INACTIVE', false);
