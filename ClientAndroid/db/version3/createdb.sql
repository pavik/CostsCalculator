CREATE TABLE cost_items (ci_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE ,ci_guid VARCHAR NOT NULL UNIQUE ,ci_name VARCHAR NOT NULL ,ci_audit_ct DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, ci_audit_ver INTEGER NOT NULL DEFAULT 0, ci_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER cost_items_after_update AFTER UPDATE ON cost_items BEGIN UPDATE cost_items SET ci_audit_ver = ci_audit_ver+1, ci_audit_mt = CURRENT_TIMESTAMP WHERE ci_id = OLD.ci_id AND ci_id = NEW.ci_id; END;

CREATE TABLE cost_item_records (cir_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE, cir_guid VARCHAR NOT NULL UNIQUE, cir_ci_guid VARCHAR NOT NULL, cir_datetime DATETIME NOT NULL, cir_sum DOUBLE NOT NULL, cir_currency VARCHAR, cir_tag VARCHAR, cir_comment VARCHAR, cir_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, cir_audit_version INTEGER NOT NULL  DEFAULT 0, cir_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER cost_items_records_after_update AFTER UPDATE ON cost_item_records BEGIN UPDATE cost_item_records SET cir_audit_version = cir_audit_version+1, cir_audit_mt = CURRENT_TIMESTAMP WHERE cir_id = OLD.cir_id AND cir_id = NEW.cir_id; END;

CREATE TABLE versions (ver_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL , ver_number INTEGER NOT NULL  UNIQUE , ver_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP);

CREATE  INDEX cir_index_ci_guid_asc ON cost_item_records (cir_ci_guid ASC);
CREATE  INDEX cir_index_datetime_asc ON cost_item_records (cir_datetime ASC);
CREATE  INDEX cir_index_datetime_desc ON cost_item_records (cir_datetime DESC);
CREATE  INDEX cir_index_currency_asc ON cost_item_records (cir_currency ASC);
CREATE  INDEX cir_index_tag_asc ON cost_item_records (cir_tag ASC);
CREATE  INDEX cir_index_audit_mt_asc ON cost_item_records (cir_audit_mt ASC);

CREATE TABLE income_items (ii_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE , ii_guid VARCHAR NOT NULL UNIQUE, ii_name VARCHAR NOT NULL, ii_currency VARCHAR, ii_audit_ct DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, ii_audit_ver INTEGER NOT NULL DEFAULT 0, ii_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER income_items_after_update AFTER UPDATE ON income_items BEGIN UPDATE income_items SET ii_audit_ver = ii_audit_ver+1, ii_audit_mt = CURRENT_TIMESTAMP WHERE ii_id = OLD.ii_id AND ii_id = NEW.ii_id; END;

CREATE TABLE income_item_records (ir_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE, ir_guid VARCHAR NOT NULL UNIQUE, ir_ii_guid VARCHAR NOT NULL, ir_datetime DATETIME NOT NULL, ir_sum DOUBLE NOT NULL, ir_currency VARCHAR, ir_tag VARCHAR, ir_comment VARCHAR, ir_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, ir_audit_version INTEGER NOT NULL  DEFAULT 0, ir_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TRIGGER income_item_records_after_update AFTER UPDATE ON income_item_records BEGIN UPDATE income_item_records SET ir_audit_version = ir_audit_version+1, ir_audit_mt = CURRENT_TIMESTAMP WHERE ir_id = OLD.ir_id AND ir_id = NEW.ir_id; END;

ALTER TABLE cost_item_records ADD COLUMN "cir_ii_guid" VARCHAR;