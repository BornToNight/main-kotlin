-- object: public.persons_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.persons_seq CASCADE;
CREATE SEQUENCE public.persons_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.persons_seq OWNER TO admindb;
-- ddl-end --

-- object: public.organizations_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.organizations_seq CASCADE;
CREATE SEQUENCE public.organizations_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.organizations_seq OWNER TO admindb;
-- ddl-end --

-- object: public.certificates_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.certificates_seq CASCADE;
CREATE SEQUENCE public.certificates_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.certificates_seq OWNER TO admindb;
-- ddl-end --

-- object: public.persons_skills | type: TABLE --
-- DROP TABLE IF EXISTS public.persons_skills CASCADE;
CREATE TABLE public.persons_skills (
	person_id bigint NOT NULL,
	skill_id bigint NOT NULL,
	CONSTRAINT persons_skills_pk PRIMARY KEY (person_id,skill_id)
);
-- ddl-end --
ALTER TABLE public.persons_skills OWNER TO admindb;
-- ddl-end --

-- object: public.skills_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.skills_seq CASCADE;
CREATE SEQUENCE public.skills_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.skills_seq OWNER TO admindb;
-- ddl-end --

-- object: public.users_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.users_seq CASCADE;
CREATE SEQUENCE public.users_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.users_seq OWNER TO admindb;
-- ddl-end --

-- object: public.refresh_tokens_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.refresh_tokens_seq CASCADE;
CREATE SEQUENCE public.refresh_tokens_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.refresh_tokens_seq OWNER TO admindb;
-- ddl-end --

-- object: public.notifications_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.notifications_seq CASCADE;
CREATE SEQUENCE public.notifications_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.notifications_seq OWNER TO admindb;
-- ddl-end --

-- object: public.permissions_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.permissions_seq CASCADE;
CREATE SEQUENCE public.permissions_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.permissions_seq OWNER TO admindb;
-- ddl-end --

-- object: public.roles_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.roles_seq CASCADE;
CREATE SEQUENCE public.roles_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.roles_seq OWNER TO admindb;
-- ddl-end --

-- object: public.permission_levels_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.permission_levels_seq CASCADE;
CREATE SEQUENCE public.permission_levels_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 9223372036854775807
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --
ALTER SEQUENCE public.permission_levels_seq OWNER TO admindb;
-- ddl-end --

-- object: public.roles_permissions_permission_levels | type: TABLE --
-- DROP TABLE IF EXISTS public.roles_permissions_permission_levels CASCADE;
CREATE TABLE public.roles_permissions_permission_levels (
	role_id bigint NOT NULL,
	permission_id bigint NOT NULL,
	permission_level_id bigint NOT NULL,
	CONSTRAINT roles_permissions_permission_levels_pk PRIMARY KEY (role_id,permission_id,permission_level_id)
);
-- ddl-end --
ALTER TABLE public.roles_permissions_permission_levels OWNER TO admindb;
-- ddl-end --

-- object: public.skills | type: TABLE --
-- DROP TABLE IF EXISTS public.skills CASCADE;
CREATE TABLE public.skills (
	skill_id bigint NOT NULL DEFAULT nextval('public.skills_seq'::regclass),
	name text NOT NULL,
	CONSTRAINT skills_pk PRIMARY KEY (skill_id)
);
-- ddl-end --
ALTER TABLE public.skills OWNER TO admindb;
-- ddl-end --

-- object: public.persons | type: TABLE --
-- DROP TABLE IF EXISTS public.persons CASCADE;
CREATE TABLE public.persons (
	person_id bigint NOT NULL DEFAULT nextval('public.persons_seq'::regclass),
	first_name text NOT NULL,
	surname text NOT NULL,
	salary_rub numeric(10,2) NOT NULL DEFAULT 0.00,
	hobby text,
	fk_organization_id bigint NOT NULL,
	fk_certificate_id bigint NOT NULL,
	CONSTRAINT persons_pk PRIMARY KEY (person_id),
	CONSTRAINT certificate_id_uq UNIQUE (fk_certificate_id)
);
-- ddl-end --
ALTER TABLE public.persons OWNER TO admindb;
-- ddl-end --

-- object: public.roles | type: TABLE --
-- DROP TABLE IF EXISTS public.roles CASCADE;
CREATE TABLE public.roles (
	role_id bigint NOT NULL DEFAULT nextval('public.roles_seq'::regclass),
	name text NOT NULL,
	CONSTRAINT roles_pk PRIMARY KEY (role_id),
	CONSTRAINT roles_name_uq UNIQUE (name)
);
-- ddl-end --
ALTER TABLE public.roles OWNER TO admindb;
-- ddl-end --

-- object: public.permissions | type: TABLE --
-- DROP TABLE IF EXISTS public.permissions CASCADE;
CREATE TABLE public.permissions (
	permission_id bigint NOT NULL DEFAULT nextval('public.permissions_seq'::regclass),
	uname text NOT NULL,
	description text NOT NULL,
	CONSTRAINT permissions_pk PRIMARY KEY (permission_id),
	CONSTRAINT permissions_uname_uq UNIQUE (uname)
);
-- ddl-end --
ALTER TABLE public.permissions OWNER TO admindb;
-- ddl-end --

-- object: public.permission_levels | type: TABLE --
-- DROP TABLE IF EXISTS public.permission_levels CASCADE;
CREATE TABLE public.permission_levels (
	permission_level_id bigint NOT NULL DEFAULT nextval('public.permission_levels_seq'::regclass),
	permission_level smallint NOT NULL,
	uname text NOT NULL,
	CONSTRAINT permission_levels_pk PRIMARY KEY (permission_level_id),
	CONSTRAINT permission_levels_permission_level_uq UNIQUE (permission_level),
	CONSTRAINT permission_levels_uname_uq UNIQUE (uname)
);
-- ddl-end --
ALTER TABLE public.permission_levels OWNER TO admindb;
-- ddl-end --

-- object: public.users | type: TABLE --
-- DROP TABLE IF EXISTS public.users CASCADE;
CREATE TABLE public.users (
	user_id bigint NOT NULL DEFAULT nextval('public.users_seq'::regclass),
	fk_role_id bigint NOT NULL,
	login text NOT NULL,
	password text NOT NULL,
	CONSTRAINT users_login_uq UNIQUE (login),
	CONSTRAINT users_pk PRIMARY KEY (user_id)
);
-- ddl-end --
ALTER TABLE public.users OWNER TO admindb;
-- ddl-end --

-- object: public.notifications | type: TABLE --
-- DROP TABLE IF EXISTS public.notifications CASCADE;
CREATE TABLE public.notifications (
	notification_id bigint NOT NULL DEFAULT nextval('public.notifications_seq'::regclass),
	person_id bigint NOT NULL,
	count bigint NOT NULL,
	CONSTRAINT person_id_uq UNIQUE (person_id),
	CONSTRAINT notifications_pk PRIMARY KEY (notification_id)
);
-- ddl-end --
ALTER TABLE public.notifications OWNER TO admindb;
-- ddl-end --

-- object: public.certificates | type: TABLE --
-- DROP TABLE IF EXISTS public.certificates CASCADE;
CREATE TABLE public.certificates (
	certificate_id bigint NOT NULL DEFAULT nextval('public.certificates_seq'::regclass),
	code text NOT NULL,
	CONSTRAINT certificates_pk PRIMARY KEY (certificate_id)
);
-- ddl-end --
ALTER TABLE public.certificates OWNER TO admindb;
-- ddl-end --

-- object: public.organizations | type: TABLE --
-- DROP TABLE IF EXISTS public.organizations CASCADE;
CREATE TABLE public.organizations (
	organization_id bigint NOT NULL DEFAULT nextval('public.organizations_seq'::regclass),
	name text NOT NULL,
	CONSTRAINT organizations_pk PRIMARY KEY (organization_id)
);
-- ddl-end --
ALTER TABLE public.organizations OWNER TO admindb;
-- ddl-end --

-- object: public.refresh_tokens | type: TABLE --
-- DROP TABLE IF EXISTS public.refresh_tokens CASCADE;
CREATE TABLE public.refresh_tokens (
	refresh_token_id bigint NOT NULL DEFAULT nextval('public.refresh_tokens_seq'::regclass),
	refresh_token text NOT NULL,
	fk_user_id bigint NOT NULL,
	CONSTRAINT user_id_uq UNIQUE (fk_user_id),
	CONSTRAINT refresh_tokens_pk PRIMARY KEY (refresh_token_id)
);
-- ddl-end --
ALTER TABLE public.refresh_tokens OWNER TO admindb;
-- ddl-end --

-- object: organization_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.persons DROP CONSTRAINT IF EXISTS organization_id_fk CASCADE;
ALTER TABLE public.persons ADD CONSTRAINT organization_id_fk FOREIGN KEY (fk_organization_id)
REFERENCES public.organizations (organization_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: certificate_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.persons DROP CONSTRAINT IF EXISTS certificate_id_fk CASCADE;
ALTER TABLE public.persons ADD CONSTRAINT certificate_id_fk FOREIGN KEY (fk_certificate_id)
REFERENCES public.certificates (certificate_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: person_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.persons_skills DROP CONSTRAINT IF EXISTS person_id_fk CASCADE;
ALTER TABLE public.persons_skills ADD CONSTRAINT person_id_fk FOREIGN KEY (person_id)
REFERENCES public.persons (person_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: skill_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.persons_skills DROP CONSTRAINT IF EXISTS skill_id_fk CASCADE;
ALTER TABLE public.persons_skills ADD CONSTRAINT skill_id_fk FOREIGN KEY (skill_id)
REFERENCES public.skills (skill_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: role_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.users DROP CONSTRAINT IF EXISTS role_id_fk CASCADE;
ALTER TABLE public.users ADD CONSTRAINT role_id_fk FOREIGN KEY (fk_role_id)
REFERENCES public.roles (role_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: user_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.refresh_tokens DROP CONSTRAINT IF EXISTS user_id_fk CASCADE;
ALTER TABLE public.refresh_tokens ADD CONSTRAINT user_id_fk FOREIGN KEY (fk_user_id)
REFERENCES public.users (user_id) MATCH SIMPLE
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: role_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.roles_permissions_permission_levels DROP CONSTRAINT IF EXISTS role_id_fk CASCADE;
ALTER TABLE public.roles_permissions_permission_levels ADD CONSTRAINT role_id_fk FOREIGN KEY (role_id)
REFERENCES public.roles (role_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: permission_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.roles_permissions_permission_levels DROP CONSTRAINT IF EXISTS permission_id_fk CASCADE;
ALTER TABLE public.roles_permissions_permission_levels ADD CONSTRAINT permission_id_fk FOREIGN KEY (permission_id)
REFERENCES public.permissions (permission_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --

-- object: permission_level_id_fk | type: CONSTRAINT --
-- ALTER TABLE public.roles_permissions_permission_levels DROP CONSTRAINT IF EXISTS permission_level_id_fk CASCADE;
ALTER TABLE public.roles_permissions_permission_levels ADD CONSTRAINT permission_level_id_fk FOREIGN KEY (permission_level_id)
REFERENCES public.permission_levels (permission_level_id) MATCH SIMPLE
ON DELETE CASCADE ON UPDATE CASCADE;
-- ddl-end --


