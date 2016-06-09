-- CREATE OR REPLACE FUNCTION get_item_categories_multiple() RETURNS SETOF refcursor AS $$
--     DECLARE
--       items refcursor;           -- Declare cursor variables
--       categories refcursor;
--     BEGIN
--       OPEN ref1 FOR SELECT city, state FROM cities WHERE state = 'CA';   -- Open the first cursor
--       RETURN NEXT ref1;                                                                              -- Return the cursor to the caller
--
--       OPEN ref2 FOR SELECT city, state FROM cities WHERE state = 'TX';   -- Open the second cursor
--       RETURN NEXT ref2;                                                                              -- Return the cursor to the caller
--     END;
--     $$ LANGUAGE plpgsql;

/* ITEM VIEW */
CREATE VIEW public.item_view AS
SELECT
	t.id,
	t.name,
	t.article,
	t.description,
	t.price,
	t.not_for_sale,
	t.in_stock,
	t.company_id,
	comp.name AS company_name,
	t.content_id
FROM (
  SELECT i.*,
          CASE WHEN ic.content_id is NULL
              THEN (SELECT c.id FROM public.content c WHERE c.is_default='Y')
              ELSE ic.content_id END AS content_id
  FROM public.item i
  LEFT OUTER JOIN public.item_content ic ON (i.id=ic.item_id AND ic.main='Y')) t
  INNER JOIN public.company comp ON t.company_id = comp.id;