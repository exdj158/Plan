import React, {useState} from 'react';
import {Col} from "react-bootstrap";
import {useDataRequest} from "../../hooks/dataFetchHook";
import {fetchServersOverview} from "../../service/networkService";
import ErrorView from "../ErrorView";
import ServersTableCard from "../../components/cards/network/ServersTableCard";
import QuickViewGraphCard from "../../components/cards/network/QuickViewGraphCard";
import QuickViewDataCard from "../../components/cards/network/QuickViewDataCard";
import ExtendableRow from "../../components/layout/extension/ExtendableRow";

const NetworkServers = () => {
    const [selectedServer, setSelectedServer] = useState(0);

    const {data, loadingError} = useDataRequest(fetchServersOverview, [])

    if (loadingError) {
        return <ErrorView error={loadingError}/>
    }

    return (
        <ExtendableRow id={'row-network-servers-0'}>
            <Col md={6}>
                <ServersTableCard loaded={Boolean(data)} servers={data?.servers || []}
                                  onSelect={(index) => setSelectedServer(index)}/>
            </Col>
            <Col md={6}>
                {data?.servers.length && <QuickViewGraphCard server={data.servers[selectedServer]}/>}
                {data?.servers.length && <QuickViewDataCard server={data.servers[selectedServer]}/>}
            </Col>
        </ExtendableRow>
    )
};

export default NetworkServers