/* ITEM VIEW */
CREATE OR REPLACE VIEW public.catalog AS
  SELECT i.*,comp.name as company_name,
          CASE WHEN ic.content_id is NULL THEN (SELECT c.id FROM public.content c WHERE c.is_default='Y') ELSE ic.content_id END AS content_id
  FROM public.item i
  LEFT OUTER JOIN public.item_content ic ON (i.id=ic.item_id AND ic.main='Y')
  INNER JOIN public.company comp ON i.company_id = comp.id
  WHERE i.not_for_sale = 'N';


CREATE OR REPLACE VIEW public.item_view AS
  SELECT i.*,comp.name as company_name
  FROM public.item i INNER JOIN public.company comp ON i.company_id = comp.id;

/* ORDER VIEW */
CREATE OR REPLACE VIEW public.order_view AS
SELECT po.id, po.uid, po.subject_id as recipient_id,
      concat_ws(' ', po.recipient_lname::varchar, po.recipient_fname::varchar, po.recipient_mname::varchar) as recipient_name,
      d.name as delivery, po.date_add as create_order_date, po.status, po.payment
FROM public.purchase_order po
INNER JOIN public.delivery d on po.delivery_id=d.id