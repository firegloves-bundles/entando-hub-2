import {Content, Tile} from "carbon-components-react";
import {useEffect, useState} from "react";
import {useParams} from "react-router";
import CatalogPageHeader from "../catalog/catalog-page-header/CatalogPageHeader";
import {
    getAllBundlesForABundleGroup,
    getSingleBundleGroup,
    getSingleCategory,
    getSingleOrganisation
} from "../../integration/Integration";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import {ModalInstallInformation} from "./modal-install-information/ModalInstallInformation";


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
    const [bundleGroup, setBundleGroup] = useState({});
    const [organisation, setOrganisation] = useState(null);
    const [category, setCategory] = useState(null);
    const [children, setChildren] = useState([]);
    const {id: bundleGroupId} = useParams();

    // fetches the bundle group
    useEffect(() => {
        const init = async () => {
            const fetchedBundleGroup = (await getSingleBundleGroup(bundleGroupId)).bundleGroup;
            setOrganisation(fetchedBundleGroup.organisationId ? (await getSingleOrganisation(fetchedBundleGroup.organisationId)).organisation : null)
            setCategory(fetchedBundleGroup.categories && fetchedBundleGroup.categories.length > 0 ? (await getSingleCategory(fetchedBundleGroup.categories[0])).category : null)
            setChildren(fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0 ? (await getAllBundlesForABundleGroup(bundleGroupId)).bundleList : [])
            setBundleGroup(fetchedBundleGroup);
        };

        init();
    }, [bundleGroupId]);

    return (
        <>
            <CatalogPageHeader/>
            <Content>
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16">
                            <EhBreadcrumb pathElements={[{path: bundleGroup.name, href: window.location.href}]}/>
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-4">
                            <Tile>
                                <div>image {bundleGroup && bundleGroup.bundleGroupdescriptionImage}</div>
                                <ModalInstallInformation bundleGroup={bundleGroup} children={children}/>
                                <div>Last Update</div>
                                <div>Link to documentation {bundleGroup && bundleGroup.documentationUrl}</div>
                                <div>{children && <BundleList children={children}/>}</div>
                            </Tile>
                        </div>
                        <div className="bx--col-lg-12">
                            <Tile>
                                <h1>{bundleGroup && bundleGroup.name}</h1>
                                <div>Version</div>
                                <div>Category {category && category.name}</div>
                                <div>Organization {organisation && organisation.name}</div>
                                <div>DESCRIPTION {bundleGroup && bundleGroup.description}</div>
                                <div>IMAGES</div>
                            </Tile>
                        </div>
                    </div>

                </div>
            </Content>
        </>
    );
};

/*
const parseGitRepoAddr = (gitRepoAddress) => {
    return gitRepoAddress ? {
        name: gitRepoAddress.substring(gitRepoAddress.lastIndexOf("/") + 1, gitRepoAddress.lastIndexOf(".")),
        gitRepoAddress
    } : {
        name: "",
        gitRepoAddress: ""
    }
}
*/

const BundleList = ({children}) => {
    const elemList = children.map((bundle, index) =>
        <li key={index.toString()}><a href={bundle.gitRepoAddress} target={"_new"}>{bundle.name}</a></li>)

    return (<div>
        List of Bundles
        <ul>{elemList}</ul>
    </div>)

}

export default BundleGroupPage;
