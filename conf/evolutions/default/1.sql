# --- !Ups

CREATE TABLE "levy_declaration" (
  "year"            INTEGER NOT NULL,
  "month"           INTEGER NOT NULL,
  "amount"          NUMERIC NOT NULL,
  "empref"          VARCHAR NOT NULL,
  "submission_type" VARCHAR NOT NULL,
  "submission_date" VARCHAR NOT NULL
);

CREATE TABLE "enrolment" (
  "gateway_id"      VARCHAR NOT NULL,
  "identifier_type" VARCHAR NOT NULL,
  "tax_id"          VARCHAR NOT NULL,
  PRIMARY KEY ("gateway_id", "identifier_type", "tax_id")
);

CREATE TABLE "auth_record" (
  "client_id"    VARCHAR NOT NULL,
  "gateway_id"   VARCHAR NOT NULL,
  "scope"        VARCHAR NOT NULL,
  "access_token" VARCHAR NOT NULL,
  "expires_at"   BIGINT  NOT NULL,
  "created_at"   BIGINT  NOT NULL
);

INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2016, 2, -100, '123/AB12345', 'amended', '2016-03-15');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2016, 2, 1000, '123/AB12345', 'original', '2016-02-21');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2016, 1, 500, '123/AB12345', 'original', '2016-01-21');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2015, 12, 600, '123/AB12345', 'original', '2015-12-21');

INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2016, 2, 750, '123/BC12345', 'original', '2016-02-21');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2016, 1, 900, '123/BC12345', 'original', '2016-01-21');
INSERT INTO "levy_declaration" ("year", "month", "amount", "empref", "submission_type", "submission_date") VALUES (2015, 12, 125, '123/BC12345', 'original', '2015-12-21');

# --- !Downs

DROP TABLE "enrolment";
DROP TABLE "levy_declaration";
DROP TABLE "auth_record";
