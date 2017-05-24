/* Функция возвращающая товар по категории */
 CREATE OR REPLACE FUNCTION get_limited_category_items(category_id varchar, offset_value integer, limit_value integer) RETURNS SETOF item AS $$
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
 	ORDER BY s.name ASC, i.name ASC
 	LIMIT limit_value
 	OFFSET offset_value;
   END;
 $$ LANGUAGE plpgsql;