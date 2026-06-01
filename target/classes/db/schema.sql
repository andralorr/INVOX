DROP TABLE IF EXISTS invoice_items CASCADE;
DROP TABLE IF EXISTS invoices      CASCADE;
DROP TABLE IF EXISTS products      CASCADE;
DROP TABLE IF EXISTS clients       CASCADE;
DROP TABLE IF EXISTS categories    CASCADE;
DROP TABLE IF EXISTS users         CASCADE;

CREATE TABLE users (
                       id                    SERIAL PRIMARY KEY,
                       username              VARCHAR(50)  NOT NULL UNIQUE,
                       password_hash         VARCHAR(128) NOT NULL,
                       company_name          VARCHAR(200) NOT NULL,
                       cui                   VARCHAR(20),
                       trade_register_number VARCHAR(30),
                       iban                  VARCHAR(34),
                       bank_name             VARCHAR(100),
                       email                 VARCHAR(150),
                       phone                 VARCHAR(30),
                       address               VARCHAR(255),
                       city                  VARCHAR(100),
                       county                VARCHAR(100)
);

CREATE TABLE categories (
                            id          SERIAL PRIMARY KEY,
                            user_id     INTEGER NOT NULL REFERENCES users(id),
                            name        VARCHAR(100) NOT NULL,
                            description VARCHAR(255)
);

CREATE TABLE products (
                          id             SERIAL PRIMARY KEY,
                          user_id        INTEGER NOT NULL REFERENCES users(id),
                          code           VARCHAR(50)  NOT NULL,
                          name           VARCHAR(150) NOT NULL,
                          unit           VARCHAR(20)  NOT NULL,
                          price          NUMERIC(12,2) NOT NULL,
                          vat_rate       NUMERIC(5,2)  NOT NULL,
                          stock_quantity INTEGER       NOT NULL DEFAULT 0,
                          category_id    INTEGER REFERENCES categories(id),
                          CONSTRAINT uq_products_user_code UNIQUE (user_id, code)
);

CREATE TABLE clients (
                         id                    SERIAL PRIMARY KEY,
                         user_id               INTEGER NOT NULL REFERENCES users(id),
                         client_type           VARCHAR(20) NOT NULL CHECK (client_type IN ('COMPANY', 'INDIVIDUAL')),
                         email                 VARCHAR(150),
                         phone                 VARCHAR(30),
                         address               VARCHAR(255),
                         city                  VARCHAR(100),
                         county                VARCHAR(100),
                         company_name          VARCHAR(200),
                         cui                   VARCHAR(20),
                         trade_register_number VARCHAR(30),
                         iban                  VARCHAR(34),
                         bank_name             VARCHAR(100),
                         first_name            VARCHAR(100),
                         last_name             VARCHAR(100),
                         cnp                   VARCHAR(13),
                         CONSTRAINT uq_clients_user_email UNIQUE (user_id, email),
                         CONSTRAINT uq_clients_user_cui   UNIQUE (user_id, cui),
                         CONSTRAINT uq_clients_user_cnp   UNIQUE (user_id, cnp)
);

CREATE TABLE invoices (
                          id          SERIAL PRIMARY KEY,
                          user_id     INTEGER NOT NULL REFERENCES users(id),
                          series      VARCHAR(10) NOT NULL,
                          number      INTEGER     NOT NULL,
                          issue_date  DATE,
                          due_date    DATE,
                          client_id   INTEGER NOT NULL REFERENCES clients(id),
                          total_net   NUMERIC(14,2) NOT NULL DEFAULT 0,
                          total_vat   NUMERIC(14,2) NOT NULL DEFAULT 0,
                          total_gross NUMERIC(14,2) NOT NULL DEFAULT 0,
                          status      VARCHAR(20) NOT NULL
                              CHECK (status IN ('DRAFT', 'ISSUED', 'PAID', 'CANCELLED')),
                          CONSTRAINT uq_invoice_series_number UNIQUE (series, number)
);

CREATE TABLE invoice_items (
                               id           SERIAL PRIMARY KEY,
                               invoice_id   INTEGER NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
                               product_id   INTEGER NOT NULL REFERENCES products(id),
                               quantity     INTEGER       NOT NULL,
                               unit_price   NUMERIC(12,2) NOT NULL,
                               vat_rate     NUMERIC(5,2)  NOT NULL,
                               net_amount   NUMERIC(14,2) NOT NULL,
                               vat_amount   NUMERIC(14,2) NOT NULL,
                               gross_amount NUMERIC(14,2) NOT NULL
);

CREATE INDEX idx_products_category   ON products(category_id);
CREATE INDEX idx_products_user       ON products(user_id);
CREATE INDEX idx_categories_user     ON categories(user_id);
CREATE INDEX idx_clients_user        ON clients(user_id);
CREATE INDEX idx_invoices_client     ON invoices(client_id);
CREATE INDEX idx_invoices_user       ON invoices(user_id);
CREATE INDEX idx_items_invoice       ON invoice_items(invoice_id);
CREATE INDEX idx_items_product       ON invoice_items(product_id);