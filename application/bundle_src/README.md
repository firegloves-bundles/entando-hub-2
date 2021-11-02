ent prj init

ent prj pbs-init

ent prj fe-push (--force)

ent prj generate-cr | ent kubectl apply -n entando -f -

or ent prj deploy / ent prj install 

