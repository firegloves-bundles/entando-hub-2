import {Content, Tile, Row, Column} from "carbon-components-react"
import {useEffect, useState} from "react"
import {useParams} from "react-router"

import {
  getAllBundlesForABundleGroup,
  getSingleBundleGroup,
  getSingleCategory,
  getSingleOrganisation
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import {ModalInstallInformation} from "./modal-install-information/ModalInstallInformation"

import "./bundle-group-page.scss"

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

const BundleGroupPage = () => {
    const [pageModel, setPageModel] = useState({
        bundleGroup: {},
        organisation: null,
        category: null,
        children: []
    })

    const {id: bundleGroupId} = useParams()


    // fetches the bundle group
    useEffect(() => {
        //TODO BE QUERY REFACTORING
        const getBundleGroupDetail = async (bundleGroupId) => {
            const pageModel = {}
            const fetchedBundleGroup = (await getSingleBundleGroup(
                bundleGroupId)).bundleGroup
            pageModel["bundleGroup"] = fetchedBundleGroup
            pageModel["organisation"] =
                fetchedBundleGroup.organisationId ? (await getSingleOrganisation(
                    fetchedBundleGroup.organisationId)).organisation : null
            pageModel["category"] =
                fetchedBundleGroup.categories && fetchedBundleGroup.categories.length
                > 0 ? (await getSingleCategory(
                    fetchedBundleGroup.categories[0])).category : null
            pageModel["children"] =
                fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0
                    ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
                    : []
            return pageModel
        };
        
        (async () => {
            const indexOf = bundleGroupId.indexOf("&") === -1 ? bundleGroupId.length : bundleGroupId.indexOf("&")
            const sanitizedId = bundleGroupId.substring(0, indexOf)
            setPageModel(await getBundleGroupDetail(sanitizedId))
        })()
    }, [bundleGroupId])


  return (
      <>
        <Content className="BundleGroupPage">
          <div className="BundleGroupPage-wrapper">
            <div className="BundleGroupPage-page">
              <div className="bx--row">
                <div className="bx--col-lg-16 BundleGroupPage-breadcrumb">
                  <EhBreadcrumb pathElements={[{
                                path: pageModel.bundleGroup.name,
                                href: window.location.href
                            }]}/>
                </div>
              </div>
              <Row className="bx--grid bx--grid--full-width">
                <Column lg={4} className="BundleGroupPage-tile">
                  <Tile>
                    <div className="BundleGroupPage-image">
                      <img
                          src={`${process.env.REACT_APP_PUBLIC_ASSETS_URL}/Logo-blue.png`}
                          alt="Entando logo"/>

                      {pageModel.bundleGroup && pageModel.bundleGroup.bundleGroupdescriptionImage}
                    </div>
                    <ModalInstallInformation bundleGroup={pageModel.bundleGroup}
                                                         children={pageModel.children}/>
                    <div className="BundleGroupPage-last-update">
                      Last Update
                      <p>09/01/2017, 09:00 </p>
                    </div>
                    <hr/>
                    <div className="BundleGroupPage-docs">
                      Link to documentation <br/>
                       <a href={pageModel.bundleGroup
                                    && pageModel.bundleGroup.documentationUrl}
                                       target="_new">Documentation</a>
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
                        Version
                        <p>1.2.0</p>

                      </Column>
                      <Column className="BundleGroupPage-specs">
                        Category
                        <p>{pageModel.category && pageModel.category.name}</p>

                      </Column>
                      <Column className="BundleGroupPage-specs">
                        Organization
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


const BundleList = ({children}) => {
    const elemList = children.map((bundle, index) =>
        <li key={index.toString()}><a href={bundle.gitRepoAddress}
                                      target={"_new"}>{bundle.name}</a></li>)

  return (
      <div className="BundleGroupPage-list-wrapper">
        <div className="BundleGroupPage-list">
          List of Bundles
        </div>
        <ul>{elemList}</ul>
      </div>
  )

}

export default BundleGroupPage
