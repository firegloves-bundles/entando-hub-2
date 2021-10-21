import React, {useEffect} from 'react'

const Notification = () => {

    const handleDeleteOrganisation = (e) => {
        console.log('Clicked on Delete Organization', e.detail)
    }

    useEffect(() => {
        window.addEventListener('delete-organisation', handleDeleteOrganisation)

        return () => {
            window.removeEventListener('delete-organisation', handleDeleteOrganisation)
        }
    }, [])

    return (
        <div>
            
        </div>
    )
}

export default Notification
