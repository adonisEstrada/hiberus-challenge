-- Payment Orders Table
CREATE TABLE IF NOT EXISTS payment_orders (
    payment_order_id VARCHAR(50) PRIMARY KEY,
    debtor_iban VARCHAR(34) NOT NULL,
    debtor_name VARCHAR(140) NOT NULL,
    debtor_bic VARCHAR(11),
    creditor_iban VARCHAR(34) NOT NULL,
    creditor_name VARCHAR(140) NOT NULL,
    creditor_bic VARCHAR(11),
    amount_value DECIMAL(19, 4) NOT NULL,
    amount_currency VARCHAR(3) NOT NULL,
    execution_date DATE NOT NULL,
    remittance_information VARCHAR(140),
    end_to_end_identification VARCHAR(35),
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    status_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT chk_amount_positive CHECK (amount_value > 0),
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT chk_priority CHECK (priority IN ('NORMAL', 'HIGH', 'URGENT'))
);

-- Index for faster lookups
CREATE INDEX IF NOT EXISTS idx_payment_orders_end_to_end ON payment_orders(end_to_end_identification);
CREATE INDEX IF NOT EXISTS idx_payment_orders_status ON payment_orders(status);
CREATE INDEX IF NOT EXISTS idx_payment_orders_execution_date ON payment_orders(execution_date);
CREATE INDEX IF NOT EXISTS idx_payment_orders_created_at ON payment_orders(created_at);

-- Comments for documentation
COMMENT ON TABLE payment_orders IS 'Payment orders following BIAN Payment Initiation Service Domain';
COMMENT ON COLUMN payment_orders.payment_order_id IS 'Unique payment order identifier (format: PO-YYYYMMDDHHMMSS{seq})';
COMMENT ON COLUMN payment_orders.end_to_end_identification IS 'Unique end-to-end transaction identifier for idempotency';
COMMENT ON COLUMN payment_orders.status IS 'Current status: PENDING, PROCESSING, COMPLETED, FAILED, REJECTED, CANCELLED';
