CREATE INDEX first_name_ix
    ON public.persons USING btree
    (first_name ASC NULLS LAST)
    WITH (deduplicate_items=True)
    TABLESPACE pg_default;