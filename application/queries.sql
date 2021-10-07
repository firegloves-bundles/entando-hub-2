

select bg.id,bg.name,bg.status,c.name,o.name from CATEGORY_BUNDLE_GROUPS  as cbg join BUNDLE_GROUP as bg on bg.id=cbg.BUNDLE_GROUPS_ID join CATEGORY c on cbg.CATEGORIES_ID=c.id join ORGANISATION o on bg.organisation_id=o.id
