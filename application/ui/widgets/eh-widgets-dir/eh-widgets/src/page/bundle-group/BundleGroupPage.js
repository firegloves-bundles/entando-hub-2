import {Content, Tile, Grid, Row, Column} from "carbon-components-react"
import {useEffect, useState} from "react"
import {useParams} from "react-router"

import {
  getAllBundlesForABundleGroup,
  getSingleBundleGroup,
  getSingleCategory,
  getSingleOrganisation
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb"
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
  const [bundleGroup, setBundleGroup] = useState({})
  const [organisation, setOrganisation] = useState(null)
  const [category, setCategory] = useState(null)
  const [children, setChildren] = useState([])
  const {id: bundleGroupId} = useParams()

  // fetches the bundle group
  useEffect(() => {
    const init = async () => {
      const fetchedBundleGroup = (await getSingleBundleGroup(
          bundleGroupId)).bundleGroup
      setOrganisation(
          fetchedBundleGroup.organisationId ? (await getSingleOrganisation(
              fetchedBundleGroup.organisationId)).organisation : null)
      setCategory(
          fetchedBundleGroup.categories && fetchedBundleGroup.categories.length
          > 0 ? (await getSingleCategory(
              fetchedBundleGroup.categories[0])).category : null)
      setChildren(
          fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0
              ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList
              : [])
      setBundleGroup(fetchedBundleGroup)
    }

    init()
  }, [bundleGroupId])

  return (
      <>
        <Content className="BundleGroupPage">
          <div className="BundleGroupPage-wrapper">
            <div className="bx--grid bx--grid--full-width BundleGroupPage-page">
              <div className="bx--row">
                <div className="bx--col-lg-16 BundleGroupPage-breadcrumb">
                  <EhBreadcrumb pathElements={[{
                    path: bundleGroup.name,
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

                      {bundleGroup && bundleGroup.bundleGroupdescriptionImage}
                    </div>
                    <ModalInstallInformation bundleGroup={bundleGroup}
                                             children={children}/>
                    <div className="BundleGroupPage-last-update">
                      Last Update
                      <p>09/01/2017, 09:00 </p>
                    </div>
                    <hr/>
                    <div className="BundleGroupPage-docs">
                      Link to documentation <br/>
                      <a href={bundleGroup
                      && bundleGroup.documentationUrl}
                         target="_new">Documentation</a>
                    </div>
                    <hr/>
                    <div>
                      {children && <BundleList children={children}/>}
                    </div>
                  </Tile>
                </Column>
                <Column lg={12}>
                  <Tile>
                    <p className="BundleGroupPage-title">
                      {bundleGroup && bundleGroup.name}
                    </p>

                    <div className="BundleGroupPage-flex">
                      <Column className="BundleGroupPage-specs">
                        Version
                        <p>1.2.0</p>

                      </Column>
                      <Column className="BundleGroupPage-specs">
                        Category
                        <p>{category && category.name}</p>

                      </Column>
                      <Column className="BundleGroupPage-specs">
                        Organization
                        <p>{organisation && organisation.name}</p>

                      </Column>

                    </div>
                    <div className="BundleGroupPage-description">
                      {bundleGroup && bundleGroup.description}
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

/*
const parseGitRepoAddr = (gitRepoAddress) =>
  {
    return gitRepoAddress ? {
      name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1,
          gitRepoAddress.lastIndexOf(".")),
      gitRepoAddress
    } : {
      name: "",
      gitRepoAddress: ""
    }
  }
*/

const BundleList = (
    {
      children
    }
) => {
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
