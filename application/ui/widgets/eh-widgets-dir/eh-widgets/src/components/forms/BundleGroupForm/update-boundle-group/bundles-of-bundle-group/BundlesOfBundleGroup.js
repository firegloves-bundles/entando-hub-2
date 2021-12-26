import {useEffect, useState} from "react"
import {Button, Tag, TextInput, Row, Column} from "carbon-components-react"
import {Add16} from '@carbon/icons-react'

import "./bundles-of-bundle-group.scss"
import {
    bundleOfBundleGroupSchema,
} from "../../../../../helpers/validation/bundleGroupSchema";
import { fillErrors } from "../../../../../helpers/validation/fillErrors";
import { BUNDLE_STATUS, GIT_REPO, BUNDLE_URL_REGEX, OPERATION } from "../../../../../helpers/constants";
import i18n from "../../../../../i18n";
/*
BUNDLE:
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
} */

const parseGitRepoAddr = (gitRepoAddress) => {
    return gitRepoAddress ? {
        name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1,
            gitRepoAddress.lastIndexOf(".")),
        gitRepoAddress
    } : {
        name: "",
        gitRepoAddress: ""
    }
}

const BundleList = ({children = [], onDeleteBundle, disabled}) => {
    const elemList = children.map(bundle => bundle.gitRepoAddress).map(
        parseGitRepoAddr).map((childrenInfo, index) =>
        <li key={index.toString()}>
            <Tag disabled={disabled}>
                {disabled && childrenInfo.name}
                {!disabled && <a href={childrenInfo.gitRepoAddress}
                   target={"_new"}>{childrenInfo.name}</a>}
                {!disabled && <span
                    className="button-delete"
                    onClick={() => onDeleteBundle(childrenInfo.gitRepoAddress)}>
                +
              </span>}
            </Tag>
        </li>
    )

    return (
        <div className="BundlesOfBundleGroup-Bundle-list">
            {/*List of Bundles*/}
            <ul className="BundlesOfBundleGroup-Bundle-list-ul">
                {elemList}
            </ul>
        </div>
    )
}

const BundlesOfBundleGroup = ({
    onAddOrRemoveBundleFromList,
    initialBundleList,
    disabled = false,
    minOneBundleError,
    bundleStatus,
    mode,
    operation,
    bundleGroupIsEditable
}) => {

    useEffect(() => {
        setBundleList(initialBundleList)
    }, [initialBundleList])

    const [bundleList, setBundleList] = useState([])
    const [gitRepo, setGitRepo] = useState("")
    const [validationResult, setValidationResult] = useState({})
    const [isUrlReqValid, setIsUrlReqValid] = useState(false)
    const [isUrlBundleRexValid, setIsUrlBundleRexValid] = useState(false)
    // TODO: vijay
    disabled = bundleGroupIsEditable && operation !== OPERATION.ADD_NEW_VERSION ? false : operation === OPERATION.ADD_NEW_VERSION ? false : disabled

    useEffect(() => {
        !bundleList.length && setIsUrlReqValid(false);
    }, [bundleList])

    const onChangeHandler = (e) => {
        const value = e.target.value
        value.trim().length > 0 ? setIsUrlReqValid(true) : setIsUrlReqValid(false)
        setGitRepo(value)
        validateBundleUrl(e)
    }

    const validateBundleUrl = (e) => {
        const value = e.target.value
        value.trim().length > 0 && new RegExp(BUNDLE_URL_REGEX).test(value) ? setIsUrlBundleRexValid(true) : setIsUrlBundleRexValid(false)
            ; (async () => {
                let validationError
                await bundleOfBundleGroupSchema.validate({ gitRepo: value }, { abortEarly: false }).catch(error => {
                    validationError = fillErrors(error)
                })
                if (validationError) {
                    value.trim().length === 0 && delete validationError.gitRepo;
                    if (value.trim().length && (bundleStatus === BUNDLE_STATUS.NOT_PUBLISHED || bundleStatus === BUNDLE_STATUS.DELETE_REQ)) {
                        validationError.gitRepo = [`${i18n.t('formValidationMsg.bundleUrlFormat')}`];
                    }
                    setValidationResult(validationError)
                }
            })()
    }

    const onAddBundle = (e) => {
        (async () => {
            //validation

            let validationError
            await bundleOfBundleGroupSchema.validate({gitRepo}, {abortEarly: false}).catch(error => {
                validationError = fillErrors(error)
            })
            if (validationError) {
                setValidationResult(validationError)
                return //don't send the form
            }

            setValidationResult({})

            if (gitRepo === "") {
                return
            }
            let newBundleList = [...bundleList, {
                name: parseGitRepoAddr(gitRepo).name,
                description: gitRepo,
                gitRepoAddress: gitRepo,
                dependencies: [],
                bundleGroups: []
            }]
            setBundleList(newBundleList)
            onAddOrRemoveBundleFromList(newBundleList)
            setGitRepo("")
            setIsUrlBundleRexValid(false)
        })()
    }

    const onDeleteBundle = (gitRepoAddress) => {
        const newBundleList = bundleList.filter(
            bundle => bundle.gitRepoAddress !== gitRepoAddress)
        setBundleList(newBundleList)
        onAddOrRemoveBundleFromList(newBundleList)
    }

    const textInputProps = {
        id: "bundle",
        labelText: bundleStatus === BUNDLE_STATUS.PUBLISH_REQ || bundleStatus === BUNDLE_STATUS.PUBLISHED ? `${i18n.t('component.bundleModalFields.addUrlBundle')} *` : `${i18n.t('component.bundleModalFields.addUrlBundle')}`
    }

    let bundleUrlErrorResult = "";
    let minOneBundle = `${i18n.t('formValidationMsg.atleastOneUrl')}`
    if (!initialBundleList.length && mode === 'Edit' && (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)) {
        /**
         * Show BUNDLE_URL_REGEX_FAIL Msg when Mode is Edit and BUNDLE_STATUS is
         * PUBLISHED OR BUNDLE_STATUS.PUBLISH_REQ, Otherwise show minOneBundle
         */
        bundleUrlErrorResult = (validationResult && validationResult.gitRepo && validationResult.gitRepo.length) ? `${i18n.t('formValidationMsg.bundleUrlFormat')}` : minOneBundle
    } else if (minOneBundleError === minOneBundle &&
        Object.keys(validationResult).length === 0 &&
        initialBundleList.length < 1 && (bundleStatus === BUNDLE_STATUS.PUBLISHED || bundleStatus === BUNDLE_STATUS.PUBLISH_REQ)) {
            bundleUrlErrorResult = minOneBundle;
    } else {
        if (!isUrlBundleRexValid) {
            bundleUrlErrorResult = validationResult["gitRepo"] &&
                validationResult["gitRepo"].join("; ")
        } else {
            bundleUrlErrorResult = null;
        }
    }
    return (
        <>
            <Row>
                <Column sm={16} md={8} lg={8}>
                    <TextInput value={gitRepo}
                               disabled={disabled}
                               onChange={onChangeHandler} {...textInputProps}
                               invalid={!isUrlReqValid ? (!!validationResult[GIT_REPO] || !!bundleUrlErrorResult) : (!isUrlBundleRexValid ? !!validationResult[GIT_REPO] : null)}
                               invalidText={bundleUrlErrorResult}
                               autoComplete={"false"}
                    />
                </Column>
                <Column sm={16} md={8} lg={8}>
                    <div className="BundlesOfBundleGroup-add-button">
                        <Button disabled={disabled} onClick={onAddBundle}
                                renderIcon={Add16}>
                            {i18n.t('component.button.add')}
                        </Button>
                    </div>
                </Column>
                <Column sm={16} md={16} lg={16}>
                    <div>
                        <BundleList children={bundleList}
                            onDeleteBundle={onDeleteBundle}
                            disabled={disabled} />
                    </div>
                </Column>
            </Row>
        </>
    )

}

export default BundlesOfBundleGroup


