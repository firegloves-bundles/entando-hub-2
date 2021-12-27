import { Content, Tile, Row, Column } from "carbon-components-react"
import { useEffect, useState } from "react"
import { useParams } from "react-router"
import { useLocation } from 'react-router-dom';

import {
  getAllBundlesForABundleGroup,
  getBundleGroupDetailsByBundleGroupVersionId,
  getSingleCategory,
  getSingleOrganisation
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import { ModalInstallInformation } from "./modal-install-information/ModalInstallInformation"
import "./bundle-group-page.scss"
import i18n from "../../i18n"
import { BREADCRUMB_VERSIONS, SLASH_VERSIONS } from "../../helpers/constants"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}


BUNDLE
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
}
 */

const formatLastUpdate = (date) => {
  return new Intl.DateTimeFormat([], { dateStyle: 'full', timeStyle: 'medium' }).format(new Date(date))
}

const BundleGroupPage = () => {
  const [pageModel, setPageModel] = useState({
    bundleGroup: {},
    organisation: null,
    category: null,
    children: []
  })

  const { id: bundleGroupVersionId } = useParams()
  const { pathname: url } = useLocation();

  const isFromVersionPage = isNavigationFromVersonsPage(url);

  // fetches the bundle group
  useEffect(() => {
    const getBundleGroupDetail = async (bundleGroupVersionId) => {
      const pageModel = {}
      const fetchedBundleGroup = (await getBundleGroupDetailsByBundleGroupVersionId(bundleGroupVersionId)).bgVersionDetails
      pageModel["bundleGroup"] = fetchedBundleGroup
      pageModel["organisation"] = fetchedBundleGroup && fetchedBundleGroup.organisationId ? (await getSingleOrganisation(
        fetchedBundleGroup.organisationId)).organisation : null
      pageModel["category"] = fetchedBundleGroup && fetchedBundleGroup.categories && fetchedBundleGroup.categories.length
        > 0 ? (await getSingleCategory(
          fetchedBundleGroup.categories[0])).category : null

          console.log("fetchedBundleGroup: ", fetchedBundleGroup);
      pageModel["children"] =
        fetchedBundleGroup && fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0 && fetchedBundleGroup.bundleGroupId
          ? (await getAllBundlesForABundleGroup(bundleGroupVersionId)).bundleList
          : []
      return pageModel
    };

    (async () => {
      const indexOf = bundleGroupVersionId.indexOf("&") === -1 ? bundleGroupVersionId.length : bundleGroupVersionId.indexOf("&")
      const sanitizedId = bundleGroupVersionId.substring(0, indexOf)
      setPageModel(await getBundleGroupDetail(sanitizedId))
    })()
  }, [bundleGroupVersionId])

  return (
      <>
        <Content className="BundleGroupPage">
          <div className="BundleGroupPage-wrapper">
            <div className="BundleGroupPage-page">
            <div className="bx--row">
              <div className="bx--col-lg-16 BundleGroupPage-breadcrumb">

                {(isFromVersionPage)
                  // Breadcrumb when navigated from Version page
                  ? <EhBreadcrumb pathElements={[{
                    path: `${BREADCRUMB_VERSIONS}`,
                    href: `${SLASH_VERSIONS}/` + pageModel.bundleGroup.bundleGroupId
                  }, {
                    path: pageModel.bundleGroup.name,
                    href: ""
                  }]} />
                  :
                  // Breadcrumb when navigated from home page
                  <EhBreadcrumb pathElements={[{
                    path: pageModel.bundleGroup.name,
                    href: window.location.href
                  }]} />}
              </div>
            </div>
              <Row className="bx--grid bx--grid--full-width">
                <Column lg={4} className="BundleGroupPage-tile">
                  <Tile>
                    <div className="BundleGroupPage-image">
                      {pageModel.bundleGroup && pageModel.bundleGroup.descriptionImage && <img
                          src={pageModel.bundleGroup && pageModel.bundleGroup.descriptionImage}
                          alt="BundleGroup Logo" />}
                    </div>
                    <ModalInstallInformation bundleGroup={pageModel.bundleGroup}
                                                         children={pageModel.children}/>
                    <div className="BundleGroupPage-last-update">
                      {i18n.t('page.bundleGroupInfo.lastUpdate')}
                      <p>{pageModel.bundleGroup && pageModel.bundleGroup.lastUpdate && formatLastUpdate(pageModel.bundleGroup.lastUpdate)}</p>
                    </div>
                    <hr/>
                    <div className="BundleGroupPage-docs">
                    {i18n.t('page.bundleGroupInfo.linkToDocument')} <br/>
                       <a href={pageModel.bundleGroup
                                    && pageModel.bundleGroup.documentationUrl}
                                       target="_new">{i18n.t('page.bundleGroupInfo.documentation')}</a>
                    </div>
                    <hr/>
                    <div>
                      {pageModel.children && <BundleList children={pageModel.children}/>}
                    </div>
                  </Tile>
                </Column>
              <Column lg={12}>
                <Tile>
                  <p className="BundleGroupPage-title">
                    {pageModel.bundleGroup && pageModel.bundleGroup.name}
                  </p>

                  <div className="BundleGroupPage-flex">
                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.version')}
                      <p>{pageModel.bundleGroup && pageModel.bundleGroup.version}</p>
                    </Column>

                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.category')}
                      <p>{pageModel.category && pageModel.category.name}</p>
                    </Column>

                    <Column className="BundleGroupPage-specs">
                      {i18n.t('page.bundleGroupInfo.organisation')}
                      <p>{pageModel.organisation && pageModel.organisation.name}</p>
                    </Column>
                  </div>

                  <div className="BundleGroupPage-description">
                    {pageModel.bundleGroup && pageModel.bundleGroup.description}
                  </div>
                </Tile>
              </Column>
              </Row>
            </div>
          </div>
        </Content>
      </>
  )
}

const BundleList = ({ children }) => {
  const elemList = children.map((bundle, index) =>
    <li key={index.toString()}><a href={bundle.gitRepoAddress}
      target={"_new"}>{bundle.name}</a></li>)

  return (
    <div className="BundleGroupPage-list-wrapper">
      <div className="BundleGroupPage-list">
        {i18n.t('page.bundleGroupInfo.listToBundles')}
      </div>
      <ul>{elemList}</ul>
    </div>
  )
}

/**
 * Check if the url contains '/versions'
 * @param {*} url 
 * @returns 
 */
const isNavigationFromVersonsPage = (url) => {
  if (url && url.indexOf(SLASH_VERSIONS) > 0) {
    return true;
  }
  return false;
}

export default BundleGroupPage
