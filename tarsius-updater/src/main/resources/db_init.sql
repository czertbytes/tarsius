DROP TABLE IF EXISTS version;
CREATE TABLE version (
    id IDENTITY PRIMARY KEY,
    val VARCHAR(32) NOT NULL
);

DROP TABLE IF EXISTS package;
CREATE TABLE package (
    id IDENTITY PRIMARY KEY,
    status SMALLINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    homepage VARCHAR(64),
    description VARCHAR(4096),
);

DROP TABLE IF EXISTS tag;
CREATE TABLE tag (
    id IDENTITY PRIMARY KEY,
    name VARCHAR(32) NOT NULL,
);

DROP TABLE IF EXISTS package_tag;
CREATE TABLE package_tag (
    package_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
);

DROP TABLE IF EXISTS parser;
CREATE TABLE parser (
    id IDENTITY PRIMARY KEY,
    class_name VARCHAR(512) NOT NULL
);

DROP TABLE IF EXISTS package_parser;
CREATE TABLE package_parser (
    package_id INTEGER NOT NULL,
    parser_id INTEGER NOT NULL,
    parser_param1 VARCHAR(1024) NOT NULL,
    parser_param2 VARCHAR(1024) NOT NULL,
    parser_param3 VARCHAR(1024) NOT NULL,
    CONSTRAINT package_fk FOREIGN KEY (package_id) REFERENCES package(id),
    CONSTRAINT parser_fk FOREIGN KEY (parser_id) REFERENCES parser(id)
);

DROP TABLE IF EXISTS distribution;
CREATE TABLE distribution (
    id IDENTITY PRIMARY KEY,
    status SMALLINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    homepage VARCHAR(64),
    version VARCHAR(64),
    release_name VARCHAR(64),
    description VARCHAR(4096)
);

DROP TABLE IF EXISTS distribution_parser;
CREATE TABLE distribution_parser (
    distribution_id INTEGER NOT NULL,
    parser_id INTEGER NOT NULL,
    CONSTRAINT distribution_parser_distribution_fk FOREIGN KEY (distribution_id) REFERENCES distribution(id),
    CONSTRAINT distribution_parser_parser_fk FOREIGN KEY (parser_id) REFERENCES parser(id)
);

DROP TABLE IF EXISTS distribution_package_version;
CREATE TABLE distribution_package_version (
    id IDENTITY PRIMARY KEY,
    distribution_id INTEGER NOT NULL,
    package_id INTEGER NOT NULL,
    version_id BIGINT NOT NULL,
    latest BOOL DEFAULT FALSE,
    created DATE NOT NULL,
    CONSTRAINT distribution_package_version_distribution_fk FOREIGN KEY (distribution_id) REFERENCES distribution(id),
    CONSTRAINT distribution_package_version_package_fk FOREIGN KEY (package_id) REFERENCES package(id),
    CONSTRAINT distribution_package_version_version_fk FOREIGN KEY (version_id) REFERENCES version(id)
);

INSERT INTO parser (class_name) VALUES ('cz.anyd.updater.parser.app.ClosureParser');
INSERT INTO parser (class_name) VALUES ('cz.anyd.updater.parser.app.HtmlPageParser');
INSERT INTO parser (class_name) VALUES ('cz.anyd.updater.parser.app.FtpDirectoryListParser');
INSERT INTO parser (class_name) VALUES ('cz.anyd.updater.parser.distro.DebianParser');
INSERT INTO parser (class_name) VALUES ('cz.anyd.updater.parser.distro.UbuntuParser');

INSERT INTO distribution (status,name,package_type) VALUES (0,'upstream',99);

INSERT INTO version (val) VALUES ('0');

INSERT INTO package (status,name) VALUES ('90','package');