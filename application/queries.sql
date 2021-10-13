

select bg.id,bg.name,bg.status,c.name,o.name from CATEGORY_BUNDLE_GROUPS  as cbg join BUNDLE_GROUP as bg on bg.id=cbg.BUNDLE_GROUPS_ID join CATEGORY c on cbg.CATEGORIES_ID=c.id join ORGANISATION o on bg.organisation_id=o.id


-- delete orphan bundles
select * from BUNDLE a LEFT JOIN  BUNDLE_BUNDLE_GROUPS b on a.id=b.BUNDLES_ID where BUNDLE_GROUPS_ID is null
