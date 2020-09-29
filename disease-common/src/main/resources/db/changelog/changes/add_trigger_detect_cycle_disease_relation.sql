CREATE OR REPLACE FUNCTION detect_cycle() RETURNS TRIGGER AS
$func$
DECLARE
  loops INTEGER;
BEGIN
   EXECUTE 'WITH RECURSIVE search_graph(ds_disease_id, ds_disease_parent_id, depth, path, cycle) AS (
        SELECT dr.ds_disease_id, dr.ds_disease_parent_id, 1,
          ARRAY[dr.ds_disease_id],
          false
        FROM ds_disease_relation dr
      UNION ALL
        SELECT dr.ds_disease_id, dr.ds_disease_parent_id, sg.depth + 1,
          path || dr.ds_disease_id,
          dr.ds_disease_id = ANY(path)
        FROM ds_disease_relation dr, search_graph sg
        WHERE dr.ds_disease_id = sg.ds_disease_parent_id AND NOT cycle
)
select count(*) FROM search_graph where cycle = TRUE' INTO loops;
IF loops > 0 THEN
  RAISE EXCEPTION 'Error - Cycle detected in ds_disease_relation table! Fix it and then retry.';
ELSE
  RETURN NEW;
END IF;
END
$func$ LANGUAGE plpgsql;

CREATE TRIGGER detect_cycle_after_insert_update
AFTER INSERT OR UPDATE ON ds_disease_relation
FOR EACH ROW EXECUTE PROCEDURE detect_cycle();