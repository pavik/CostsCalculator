CREATE TABLE income_items (ii_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE , ii_guid VARCHAR NOT NULL UNIQUE, ii_name VARCHAR NOT NULL, ii_currency VARCHAR, ii_audit_ct DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, ii_audit_ver INTEGER NOT NULL DEFAULT 0, ii_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER income_items_after_update AFTER UPDATE ON income_items BEGIN UPDATE income_items SET ii_audit_ver = ii_audit_ver+1, ii_audit_mt = CURRENT_TIMESTAMP WHERE ii_id = OLD.ii_id AND ii_id = NEW.ii_id; END;

CREATE TABLE income_item_records (ir_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE, ir_guid VARCHAR NOT NULL UNIQUE, ir_ii_guid VARCHAR NOT NULL, ir_datetime DATETIME NOT NULL, ir_sum DOUBLE NOT NULL, ir_currency VARCHAR, ir_tag VARCHAR, ir_comment VARCHAR, ir_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, ir_audit_version INTEGER NOT NULL  DEFAULT 0, ir_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER income_item_records_after_update AFTER UPDATE ON income_item_records BEGIN UPDATE income_item_records SET ir_audit_version = ir_audit_version+1, ir_audit_mt = CURRENT_TIMESTAMP WHERE ir_id = OLD.ir_id AND ir_id = NEW.ir_id; END;

ALTER TABLE cost_item_records ADD COLUMN "cir_ii_guid" VARCHAR;