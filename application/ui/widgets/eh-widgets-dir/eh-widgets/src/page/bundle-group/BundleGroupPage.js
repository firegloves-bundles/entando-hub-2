import {Content} from "carbon-components-react";
import {useState, useEffect} from "react";
import {useParams} from "react-router";
import CatalogPageHeader from "../catalog/catalog-page-header/CatalogPageHeader";
import {getSingleBundleGroup, getSingleCategory, getSingleOrganisation} from "../../integration/Integration";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";


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
            //setChildren(fetchedBundleGroup.children && fetchedBundleGroup.children.length > 0 ? (await get(fetchedBundleGroup.categories[0])).category : null)
            setBundleGroup(fetchedBundleGroup);
        };

        init();
    }, [bundleGroupId]);

    console.log(bundleGroup);

    return (
        <>
            <CatalogPageHeader/>
            <Content>
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16">
                            <EhBreadcrumb pathElements={[]}/>
                        </div>
                    </div>
                    <div>
                        <div>IMAGE</div>
                        <div>Install Button</div>
                        <div>Last Update</div>
                        <div>Link to Repository</div>
                        <div>Link to documentation {bundleGroup.documentationUrl}</div>
                        <div>List of Bundles {bundleGroup.children}</div>
                    </div>
                    <div>
                        <div>Product Details</div>
                        <div>Version</div>
                        <div>Category {JSON.stringify(category)}</div>
                        <div>Organization {JSON.stringify(organisation)}</div>
                        <div>DESCRIPTION {bundleGroup.description}</div>
                        <div>IMAGES</div>
                    </div>
                </div>
            </Content>
        </>
    );
};

export default BundleGroupPage;
