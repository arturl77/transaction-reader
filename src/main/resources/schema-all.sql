CREATE TABLE IF NOT EXISTS exchange_rate (
  id INT AUTO_INCREMENT PRIMARY KEY,
  currency CHAR (3) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  rate DECIMAL NOT NULL
);

CREATE TABLE IF NOT EXISTS payment_transaction (
  id INT AUTO_INCREMENT PRIMARY KEY,
  transaction_date DATE NOT NULL,
  transaction_id INT NOT NULL,
  title VARCHAR (250),
  amount_pln DECIMAL NOT NULL,
  amount_eur DECIMAL
);