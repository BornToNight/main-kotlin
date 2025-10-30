CREATE INDEX uname_ix
    ON public.permissions USING btree
    (uname ASC NULLS LAST)
    WITH (deduplicate_items=True)
    TABLESPACE pg_default;