import * as Yup from "yup"
import { DOCUMENTATION_ADDRESS_URL_REGEX } from "../constants"

export const newVersionBundleGroupSchema = Yup.object().shape({
    description: Yup.string()
        .min(3, "minDescription")
        .max(600, "maxDescription")
        .required("descriptionRequired"),
    documentationUrl: Yup.string()
        .matches(
            DOCUMENTATION_ADDRESS_URL_REGEX,
            "docFormat"
        )
        .max(255, "max255Char")
        .required("docRequired"),
    status: Yup.string().required("statusRequired"),
    version: Yup.string().matches(/^[v]?([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)\.([0-9]|[1-9][0-9]*)(?:-([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?(?:\+([0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*))?$/gm, "versionFormat").required("versionRequired"),
    bundles: Yup.array().of(
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
