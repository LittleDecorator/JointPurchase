/* Функция возвращающая товар по категории */
 CREATE OR REPLACE FUNCTION get_limited_category_items(categoryId varchar, companyId varchar, offset_value integer, limit_value integer) RETURNS SETOF item AS $$
   DECLARE
   BEGIN
   RETURN QUERY
	-- соберем категории
	WITH RECURSIVE stash AS (
		SELECT id, parent_id, name FROM category WHERE id = categoryId
	UNION
		SELECT c.id, c.parent_id, c.name FROM category c JOIN stash ON c.parent_id = stash.id
	)

 	SELECT i.* FROM item i
 	INNER JOIN category_item ci ON i.id=ci.item_id
 	INNER JOIN stash s ON ci.category_id=s.id
 	WHERE i.company_id = companyId
 	ORDER BY s.name ASC, i.status , i.name ASC
 	LIMIT limit_value
 	OFFSET offset_value;
   END;
 $$ LANGUAGE plpgsql;

/* функция получерия корневых категорий по компании */
CREATE OR REPLACE FUNCTION public.get_root_categories_for_company(companyid character varying)
  RETURNS SETOF category AS
$BODY$
   BEGIN
   RETURN QUERY
	WITH RECURSIVE r AS (
		SELECT * FROM category WHERE id in (
			select distinct COALESCE(c.parent_id, c.id) from item i
			inner join category_item ci on i.id=ci.item_id
			inner join category c on c.id=ci.category_id
			where i.company_id= companyid)
		UNION ALL
		SELECT c.* FROM category c JOIN r ON c.id = r.parent_id
	)
	SELECT * FROM r where r.parent_id is null;
   END; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.get_root_categories_for_company(character varying)
  OWNER TO postgres;



/* КОСТЫЛЬ */
CREATE OR REPLACE FUNCTION public.get_limited_category_items(
    category_id character varying,
    companyid character varying,
    offset_value integer,
    limit_value integer)
  RETURNS SETOF item AS
$BODY$
   DECLARE
   BEGIN
   RETURN QUERY
	-- соберем категории
	WITH RECURSIVE stash AS (
		SELECT id, parent_id, name FROM category WHERE id = category_id
	UNION
		SELECT c.id, c.parent_id, c.name FROM category c JOIN stash ON c.parent_id = stash.id
	)

	SELECT i.* FROM item i
	INNER JOIN category_item ci ON i.id=ci.item_id
	INNER JOIN stash s ON ci.category_id=s.id
	WHERE i.company_id = companyid
	ORDER BY s.name ASC, i.status, i.name ASC
	LIMIT limit_value
	OFFSET offset_value;
   END;
 $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION public.get_limited_category_items(character varying, integer, integer)
  OWNER TO postgres;
