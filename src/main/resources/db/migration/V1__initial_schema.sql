-- Create users table (for authentication)
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create drivers table
CREATE TABLE drivers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    license_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(50) NOT NULL,
    deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create vehicles table
CREATE TABLE vehicles (
    id BIGSERIAL PRIMARY KEY,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    make VARCHAR(100),
    model VARCHAR(100),
    year INTEGER,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create vehicle assignments table
CREATE TABLE vehicle_assignments (
    id BIGSERIAL PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unassigned_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    assigned_by BIGINT NOT NULL,
    FOREIGN KEY (driver_id) REFERENCES drivers(id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    FOREIGN KEY (assigned_by) REFERENCES users(id)
);

-- Create indexes for performance
CREATE INDEX idx_drivers_status ON drivers(status) WHERE deleted = false;
CREATE INDEX idx_drivers_license ON drivers(license_number);
CREATE INDEX idx_vehicles_registration ON vehicles(registration_number);
CREATE INDEX idx_assignments_driver ON vehicle_assignments(driver_id);
CREATE INDEX idx_assignments_vehicle ON vehicle_assignments(vehicle_id);
CREATE INDEX idx_assignments_active ON vehicle_assignments(is_active) WHERE is_active = true;

-- Create unique constraints for active assignments (one active assignment per driver/vehicle)
CREATE UNIQUE INDEX unique_active_driver ON vehicle_assignments(driver_id)
    WHERE is_active = true;
CREATE UNIQUE INDEX unique_active_vehicle ON vehicle_assignments(vehicle_id)
    WHERE is_active = true;
