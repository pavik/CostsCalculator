CREATE TABLE cost_items2 (ci_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE ,ci_guid VARCHAR NOT NULL UNIQUE ,ci_name VARCHAR NOT NULL ,ci_audit_ct DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, ci_audit_ver INTEGER NOT NULL DEFAULT 0, ci_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
INSERT INTO cost_items2 (ci_guid, ci_name, ci_audit_ct, ci_audit_ver) SELECT ci_guid, ci_name, ci_audit_ct, ci_audit_ver FROM cost_items;
DROP TRIGGER cost_items_after_update;
DROP TABLE cost_items;
ALTER TABLE cost_items2 RENAME TO cost_items;
CREATE TRIGGER cost_items_after_update AFTER UPDATE ON cost_items BEGIN UPDATE cost_items SET ci_audit_ver = ci_audit_ver+1, ci_audit_mt = CURRENT_TIMESTAMP WHERE ci_id = OLD.ci_id AND ci_id = NEW.ci_id; END;


CREATE TABLE cost_item_records2 (cir_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL UNIQUE, cir_guid VARCHAR NOT NULL UNIQUE, cir_ci_guid VARCHAR NOT NULL, cir_datetime DATETIME NOT NULL, cir_sum DOUBLE NOT NULL, cir_currency VARCHAR, cir_tag VARCHAR, cir_comment VARCHAR, cir_audit_ct DATETIME NOT NULL  DEFAULT CURRENT_TIMESTAMP, cir_audit_version INTEGER NOT NULL  DEFAULT 0, cir_audit_mt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP);
INSERT INTO cost_item_records2 (cir_guid, cir_ci_guid, cir_datetime, cir_sum, cir_currency, cir_tag, cir_comment, cir_audit_ct, cir_audit_version) SELECT cir_guid, cir_ci_guid, cir_datetime, cir_sum, cir_currency, cir_tag, cir_comment, cir_audit_ct, cir_audit_version FROM cost_item_records;
DROP TABLE cost_item_records;
ALTER TABLE cost_item_records2 RENAME TO cost_item_records;
CREATE TRIGGER cost_items_records_after_update AFTER UPDATE ON cost_item_records BEGIN UPDATE cost_item_records SET cir_audit_version = cir_audit_version+1, cir_audit_mt = CURRENT_TIMESTAMP WHERE cir_id = OLD.cir_id AND cir_id = NEW.cir_id; END;

CREATE  INDEX cir_index_ci_guid_asc ON cost_item_records (cir_ci_guid ASC);
CREATE  INDEX cir_index_datetime_asc ON cost_item_records (cir_datetime ASC);
CREATE  INDEX cir_index_datetime_desc ON cost_item_records (cir_datetime DESC);
CREATE  INDEX cir_index_currency_asc ON cost_item_records (cir_currency ASC);
CREATE  INDEX cir_index_tag_asc ON cost_item_records (cir_tag ASC);
CREATE  INDEX cir_index_audit_mt_asc ON cost_item_records (cir_audit_mt ASC);