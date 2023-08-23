import {TeamType} from "../models/team";

let teamById: TeamType;

const setTeamById = (team: TeamType) => {
    teamById = team;
}

const getTeamById = () : TeamType => {
    return teamById;
}

export {
    setTeamById,
    getTeamById,
}
