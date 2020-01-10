CREATE TABLE "version" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "version" TEXT NOT NULL,
  "update_date" TEXT NOT NULL,
  "status" integer NOT NULL
);

CREATE TABLE "user" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "username" TEXT NOT NULL,
  "password" TEXT NOT NULL,
  "name" TEXT NOT NULL,
  "status" integer NOT NULL,
  "admin" integer NOT NULL,
  "create_user_id" TEXT NOT NULL,
  "create_date" TEXT NOT NULL,
  "update_user_id" TEXT NOT NULL,
  "update_date" TEXT NOT NULL,
  UNIQUE ("username")
);

CREATE TABLE "user_admin_captcha" (
  "id" text NOT NULL,
  "username" TEXT NOT NULL,
  "captcha" TEXT NOT NULL,
  "send_date" TEXT NOT NULL,
  "validate_minutes" integer NOT NULL,
  "status" integer NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "user_plugin" (
  "id" TEXT NOT NULL,
  "user_id" TEXT NOT NULL,
  "system_version" TEXT NOT NULL,
  "create_date" TEXT NOT NULL,
  "update_date" TEXT NOT NULL,
  "group_name" TEXT NOT NULL,
  "name" TEXT NOT NULL,
  "description" TEXT NOT NULL,
  "template_path" TEXT,
  "template_content" TEXT NOT NULL,
  "generator" TEXT NOT NULL,
  "generator_source_content" TEXT NOT NULL,
  "generator_content" TEXT NOT NULL,
  "file_relative_dir" TEXT NOT NULL,
  "file_suffix" TEXT,
  "prefix" TEXT,
  "suffix" TEXT,
  "plugin_path" TEXT,
  "dependencies" TEXT,
  "status" integer NOT NULL,
  "custom" integer NOT NULL,
  PRIMARY KEY ("id")
);
CREATE TABLE "user_custom_plugin_temp" (
  "id" TEXT NOT NULL,
  "user_plugin_id" text,
  "group_name" TEXT NULL,
  "name" TEXT NULL,
  "description" TEXT NULL,
  "template_content" TEXT NULL,
  "generator" TEXT NULL,
  "generator_source_content" TEXT NULL,
  "generator_content" TEXT NULL,
  "file_relative_dir" TEXT NULL,
  "file_suffix" TEXT,
  "prefix" TEXT,
  "suffix" TEXT,
  "dependencies" TEXT,
  "custom" integer,
  PRIMARY KEY ("id")
);
CREATE TABLE "user_plugin_change_history" (
  "id" TEXT NOT NULL,
  "plugin_id" TEXT NOT NULL,
  "plugin_group_name" TEXT NOT NULL,
  "plugin_name" TEXT NOT NULL,
  "plugin_description" TEXT NOT NULL,
  "plugin_dependencies" TEXT NULL,
  "user_id" TEXT NOT NULL,
  "update_date" TEXT NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "user_plugin_change_history_detail" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "change_history_id" TEXT NOT NULL,
  "change_field_comment" TEXT NOT NULL,
  "change_field_name" TEXT NOT NULL,
  "change_before" TEXT NOT NULL,
  "change_after" TEXT NOT NULL
);
CREATE TABLE "user_active_check_code" (
  "id" text NOT NULL,
  "user_id" text NOT NULL,
  "check_code" TEXT NOT NULL,
  "send_date" TEXT NOT NULL,
  "validate_minutes" integer NOT NULL,
  "status" integer NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "check_code" UNIQUE ("check_code")
);

CREATE TABLE "log_login" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "user_id" TEXT NOT NULL,
  "login_date" TEXT NOT NULL,
  "login_ip" TEXT NOT NULL,
  "exit_date" TEXT
);

CREATE TABLE "log_access" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "user_id" TEXT NOT NULL,
  "system_version" TEXT NOT NULL,
  "access_date" TEXT NOT NULL,
  "time" text NOT NULL,
  "parameters" TEXT NOT NULL,
  "url" TEXT NOT NULL,
  "result_type" TEXT NOT NULL,
  "result" TEXT NOT NULL,
  "exception" TEXT
);

CREATE TABLE "log_generate" (
  "id" text NOT NULL,
  "user_id" text NOT NULL,
  "generate_start_date" text NOT NULL,
  "generate_stop_date" TEXT NOT NULL,
  "generate_time" TEXT,
  "generate_parameter_database" TEXT NOT NULL,
  "generate_parameter_ssh" TEXT,
  "generate_parameter_parameter" TEXT,
  "generate_result" integer NOT NULL,
  "fail_reason" TEXT,
  PRIMARY KEY ("id")
);

CREATE TABLE "plugin" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "system_version" TEXT NOT NULL,
  "create_date" TEXT NOT NULL,
  "create_user_id" TEXT,
  "create_user_name" TEXT NOT NULL,
  "update_date" TEXT NOT NULL,
  "update_user_id" TEXT,
  "update_user_name" TEXT NOT NULL,
  "group_name" TEXT NOT NULL,
  "name" TEXT NOT NULL,
  "description" TEXT NOT NULL,
  "template_path" TEXT NOT NULL,
  "template_content" TEXT NOT NULL,
  "generator" TEXT NOT NULL,
  "generator_source_content" TEXT NOT NULL,
  "generator_content" TEXT NOT NULL,
  "file_relative_dir" TEXT NOT NULL,
  "file_suffix" TEXT,
  "prefix" TEXT,
  "suffix" TEXT,
  "plugin_path" TEXT NOT NULL,
  "dependencies" TEXT NULL,
  "status" integer NOT NULL,
  UNIQUE ("name", "group_name")
);

CREATE TABLE "plugin_change_history" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "plugin_id" TEXT NOT NULL,
  "update_user_id" TEXT,
  "update_user_name" TEXT NOT NULL,
  "update_date" TEXT NOT NULL
);

CREATE TABLE "plugin_change_history_detail" (
  "id" TEXT NOT NULL PRIMARY KEY,
  "change_history_id" TEXT NOT NULL,
  "change_field_comment" TEXT NOT NULL,
  "change_field_name" TEXT NOT NULL,
  "change_before" TEXT NOT NULL,
  "change_after" TEXT NOT NULL
);

CREATE TABLE "user_password_check_code" (
  "id" text NOT NULL,
  "user_id" text NOT NULL,
  "check_code" TEXT NOT NULL,
  "send_date" TEXT NOT NULL,
  "validate_minutes" integer NOT NULL,
  "status" integer NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "check_code" UNIQUE ("check_code")
);

CREATE TABLE "persistent_logins" (
  "username" text NOT NULL,
  "series" TEXT NOT NULL,
  "token" TEXT NOT NULL,
  "last_used" text NOT NULL,
  PRIMARY KEY ("username")
);

CREATE TABLE "init_wizard_current_step" (
  "id" TEXT NOT NULL,
  "current_step_num" INTEGER NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "init_wizard_step" (
  "id" text NOT NULL,
  "step_num" INTEGER NOT NULL,
  "step_name" TEXT NOT NULL,
  "step_url" TEXT NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "step_num_unique" UNIQUE ("step_num")
);

INSERT INTO "init_wizard_step" VALUES (1, 1, '许可协议', '/init/wizard/license');
INSERT INTO "init_wizard_step" VALUES (2, 2, '配置邮件服务器', '/init/wizard/mail');
INSERT INTO "init_wizard_step" VALUES (3, 3, '设置超级用户', '/init/wizard/admin');
INSERT INTO "init_wizard_step" VALUES (4, 4, '选择启用插件', '/init/wizard/enablePlugins');
INSERT INTO "init_wizard_step" VALUES (5, 5, '配置Geetest', '/init/wizard/geetest');

CREATE TABLE "init_wizard_complete" (
  "id" text NOT NULL,
  "status" integer NOT NULL,
  PRIMARY KEY ("id")
);

CREATE TABLE "config_email" (
  "id" text NOT NULL,
  "email_from" text NOT NULL,
  "password" TEXT NOT NULL,
  "host" TEXT NOT NULL,
  "port" integer NOT NULL,
  "ssl" integer NOT NULL,
  PRIMARY KEY ("id")
);

INSERT INTO "config_email" VALUES (1, '', '', '', 25, 0);

CREATE TABLE "config_geetest" (
  "id" text NOT NULL,
  "key" text NOT NULL,
  "weight" integer NOT NULL DEFAULT 1,
  PRIMARY KEY ("id"),
  CONSTRAINT "key" UNIQUE ("key")
);

INSERT INTO "config_geetest" VALUES ('ca98f9688c2f3c5abee8f14b496e5375', 'be29c5e6d597b97a2a27e3071245dcd3', 1);

CREATE TABLE "config_system_parameter" (
  "id" text NOT NULL,
  "access_log_remain_days" integer NOT NULL,
  "user_active_minutes" integer NOT NULL,
  "reset_password_minutes" integer NOT NULL,
  "captcha_expire_minutes" integer NOT NULL,
  "enable_user_custom_plugin" integer NOT NULL,
  "max_task_count" integer NOT NULL,
  PRIMARY KEY ("id")
);

INSERT INTO "config_system_parameter" VALUES (1, 30, 30, 30, 30, 1, 10);
CREATE TABLE "connection" (
  "id" text NOT NULL,
  "user_id" text NOT NULL,
  "connection_name" TEXT NOT NULL,
  PRIMARY KEY ("id")
);
CREATE TABLE "connection_database" (
  "id" text NOT NULL,
  "connection_id" TEXT NOT NULL,
  "type" TEXT NOT NULL,
  "driver" TEXT NOT NULL,
  "url" TEXT NOT NULL,
  "port" TEXT NOT NULL,
  "username" TEXT NOT NULL,
  "password" TEXT NOT NULL,
  "database_name" TEXT NOT NULL,
  "connection_url" TEXT NOT NULL,
  PRIMARY KEY ("id")
);
CREATE TABLE "connection_generate_parameter" (
  "id" text NOT NULL,
  "connection_id" text NOT NULL,
  "table_names" TEXT,
  "remove_prefixs" TEXT,
  "plugin_names" TEXT,
  PRIMARY KEY ("id")
);
CREATE TABLE "connection_ssh" (
  "id" text NOT NULL,
  "connection_id" text NOT NULL,
  "host" TEXT,
  "port" TEXT,
  "username" TEXT,
  "password" TEXT,
  PRIMARY KEY ("id")
);

CREATE TABLE "test_generate_code_test_table" (
  "id" INTEGER NOT NULL,
  "c_int_column" integer,
  "c_text_column" TEXT,
  PRIMARY KEY ("id")
);