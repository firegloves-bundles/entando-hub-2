import * as Yup from "yup"
import {BUNDLE_STATUS, BUNDLE_URL_REGEX, BUNDLE_SRC_URL_REGEX, CONTACT_URL_REGEX, DOCUMENTATION_ADDRESS_URL_REGEX, VERSION_REGEX} from "../constants"

export const versionString = Yup.string().matches(VERSION_REGEX, "versionFormat").required("versionRequired");
export const gitSrcRepoAddressRule = Yup.string().trim().matches(BUNDLE_SRC_URL_REGEX, {excludeEmptyString:true, message:"bundleSrcUrlFormat"}).max(255, "max255Char");
export const bundleListSchema = Yup.array().of(
    Yup.object().shape({
        bundleGroups: Yup.array().of(Yup.string()),
        dependencies: Yup.array().of(Yup.string()),
        description: Yup.string(),
        gitRepoAddress: Yup.string().trim(),
        gitSrcRepoAddress: gitSrcRepoAddressRule,
        name: Yup.string().required()
    })
);

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
  children: bundleListSchema
    .min(1, "atleastOneUrl"),
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
    bundles: bundleListSchema
        .when(['displayContactUrl','status'], {
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

export const bundleUrlSchema = Yup.object().shape({
  gitRepo: Yup.string()
      .trim()
      .required("bundleUrlRequired")
      .matches(
        BUNDLE_URL_REGEX,
          "bundleUrlFormat"
      )
      .max(255, "max255Char")
})

export const bundleSrcUrlSchema = Yup.object().shape({
    gitSrcRepo: gitSrcRepoAddressRule
})

