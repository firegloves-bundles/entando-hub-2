import * as Yup from "yup"

export const newVersionBundleGroupSchema = Yup.object().shape({
    description: Yup.string()
        .min(3, "minDescription")
        .max(600, "maxDescription")
        .required("descriptionRequired"),
    documentationUrl: Yup.string()
        .matches(
            /[-a-zA-Z0-9@:%_+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_+.~#?&//=]*)?/gi,
            "docFormat"
        )
        .required("docRequired"),
    status: Yup.string().required("statusRequired"),
    version: Yup.string().matches(/^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "versionFormat").required("versionRequired"),
    children: Yup.array().of(
        Yup.object().shape({
            bundleGroups: Yup.array().of(Yup.string()),
            dependencies: Yup.array().of(Yup.string()),
            description: Yup.string(),
            gitRepoAddress: Yup.string(),
            name: Yup.string().required(),
        })
    ).min(1, "atleastOneUrl"),
    categories: Yup.array()
        .of(Yup.string())
        .required("categoryRequired"),
})
