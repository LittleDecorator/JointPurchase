-- create type item_type as(
-- 	id varchar(37),
-- 	name varchar(255),
-- 	article varchar(30),
-- 	description varchar(2000),
-- 	price numeric(20,2),
-- 	not_for_sale character(1),
-- 	in_stock integer,
-- 	company_id varchar(37),
-- 	company_name varchar(50),
-- 	content_id varchar(128)
-- 	);
--
-- CREATE OR REPLACE FUNCTION get_items_table() RETURNS SETOF item_type AS
-- $$
--   DECLARE
--      def_id varchar(50);
--   BEGIN
--
-- 		SELECT c.id INTO def_id FROM public.content c WHERE c.is_default='Y';
--
-- 		RETURN QUERY
-- 			SELECT i.id, i.name, i.article, i.description, i.price, i.not_for_sale, i.in_stock, i.company_id, comp.name AS company_name,
-- 				CASE WHEN ic.content_id IS NULL THEN def_id ELSE ic.content_id END AS content_id
-- 			FROM public.item i
-- 				LEFT OUTER JOIN public.item_content ic ON (i.id=ic.item_id AND ic.main='Y')
-- 				INNER JOIN public.company comp ON i.company_id = comp.id;
--   END;
-- $$ LANGUAGE plpgsql;

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
  SELECT i.*,comp.name as company_name,
          CASE WHEN ic.content_id is NULL THEN (SELECT c.id FROM public.content c WHERE c.is_default='Y') ELSE ic.content_id END AS content_id
  FROM public.item i
  LEFT OUTER JOIN public.item_content ic ON (i.id=ic.item_id AND ic.main='Y')
  INNER JOIN public.company comp ON i.company_id = comp.id;