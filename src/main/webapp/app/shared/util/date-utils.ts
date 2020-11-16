import moment from 'moment';

import { APP_LOCAL_DATETIME_FORMAT, APP_LOCAL_DATETIME_FORMAT_Z, APP_ARTICLE_TIMESTAMP_FORMAT } from 'app/config/constants';

export const convertDateTimeFromServer = date => (date ? moment(date).format(APP_LOCAL_DATETIME_FORMAT) : null);

export const convertDateTimeToServer = date => (date ? moment(date, APP_LOCAL_DATETIME_FORMAT_Z).toDate() : null);

export const displayDefaultDateTime = () => moment().startOf('day').format(APP_LOCAL_DATETIME_FORMAT);

export const convertDateTimeForArticle = date => (date ? moment(date).format(APP_ARTICLE_TIMESTAMP_FORMAT) : null);

export const getDayCroatian = date =>
  ['ponedjeljak', 'utorak', 'srijeda', 'cetvrtak', 'petak', 'subota', 'nedjelja'][
    moment(date, APP_LOCAL_DATETIME_FORMAT_Z).toDate().getDay()
  ];
