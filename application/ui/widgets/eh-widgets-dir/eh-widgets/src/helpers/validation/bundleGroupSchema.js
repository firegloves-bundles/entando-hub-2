import * as Yup from "yup"
import {BUNDLE_STATUS, BUNDLE_URL_REGEX, CONTACT_URL_REGEX, DOCUMENTATION_ADDRESS_URL_REGEX, VERSION_REGEX} from "../constants"

export const versionString = Yup.string().matches(VERSION_REGEX, "versionFormat").required("versionRequired");

export const bundleGroupSchema = Yup.object().shape({
  name: Yup.string()
    .min(3, "min3Char")
    .max(25, "max25Char")
    .required("nameRequired"),
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
  displayContactUrl: Yup.boolean()
    .nullable(),
   contactUrl: Yup.string()
    .nullable()
    .when('displayContactUrl', {
        is: (displayContactUrl) => (displayContactUrl === true),
        then: Yup.string()
            .required('contactUrlRequired')
            .matches(
                CONTACT_URL_REGEX,
                "contactUrlFormat"
            )
    })
    .max(255, "max255Char"),
  status: Yup.string().required("statusRequired"),
  version: versionString,
  children: Yup.array().of(
    Yup.object().shape({
      bundleGroups: Yup.array().of(Yup.string()),
      // bundleId: Yup.number().positive().integer(),
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

export const versionBundleGroupSchema = Yup.object().shape({
    description: Yup.string()
        .min(3, "minDescription")
        .max(600, "maxDescription")
        .required("descriptionRequired"),
    displayContactUrl: Yup.boolean()
      .nullable(),
    contactUrl: Yup.string()
        .nullable()
        .when('displayContactUrl', {
            is: (displayContactUrl) => (displayContactUrl === true),
            then: Yup.string()
                .required('contactUrlRequired')
                .matches(
                    CONTACT_URL_REGEX,
                    "contactUrlFormat"
                )
        })
        .max(255, "max255Char"),
    documentationUrl: Yup.string()
        .matches(
            DOCUMENTATION_ADDRESS_URL_REGEX,
            "docFormat"
        )
        .max(255, "max255Char")
        .required("docRequired"),
    version: versionString,
    status: Yup.string().required("statusRequired"),
    bundles: Yup.array().of(
        Yup.object().shape({
            bundleGroups: Yup.array().of(Yup.string()),
            // bundleId: Yup.number().positive().integer(),
            dependencies: Yup.array().of(Yup.string()),
            description: Yup.string(),
            gitRepoAddress: Yup.string(),
            name: Yup.string().required(),
        })
    ).when(['displayContactUrl','status'], {
        is: (displayContactUrl, status) => {
            return (displayContactUrl !== true) && ((status === BUNDLE_STATUS.PUBLISH_REQ) || (status === BUNDLE_STATUS.PUBLISHED))
        },
        then: Yup.array().min(1, "atleastOneUrl")},
    )
}, [['status','displayContactUrl', 'contactUrl', 'bundles']]
)

export const newBundleGroupSchema = Yup.object().shape({
  name: Yup.string()
      .min(3, "min3Char")
      .max(25, "max25Char")
      .required("nameRequired"),
  versionDetails: versionBundleGroupSchema.required(),
  categories: Yup.array()
      .of(Yup.string())
      .required("categoryRequired"),
})

export const bundleOfBundleGroupSchema = Yup.object().shape({
  gitRepo: Yup.string()
      .required("bundleUrlRequired")
      .matches(
        BUNDLE_URL_REGEX,
          "bundleUrlFormat"
      )
      .max(255, "max255Char")
})
