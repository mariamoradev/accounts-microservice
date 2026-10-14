-- =============================================
-- Tabla del microservicio accounts (base crud_usuarios en TiDB)
-- =============================================

CREATE TABLE IF NOT EXISTS crud_usuarios.accounts (
    id              VARCHAR(36)    NOT NULL PRIMARY KEY,
    user_id         VARCHAR(36)    NOT NULL UNIQUE,
    account_number  VARCHAR(20)    NULL UNIQUE,
    email           VARCHAR(150)   NOT NULL,
    balance         DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Si la tabla ya existia con la version corta de la guia del grupo, ejecutar
-- estas tres instrucciones UNA POR UNA en el SQL Editor de TiDB:
-- ALTER TABLE crud_usuarios.accounts ADD COLUMN account_number VARCHAR(20) NULL;
-- ALTER TABLE crud_usuarios.accounts ADD COLUMN balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00;
-- CREATE UNIQUE INDEX uk_accounts_number ON crud_usuarios.accounts (account_number);
