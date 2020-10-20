import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';
import { IArticle } from 'app/shared/model/article.model';

export interface IComment {
  id?: number;
  posted?: string;
  content?: string;
  author?: IUser;
  article?: IArticle;
}

export const defaultValue: Readonly<IComment> = {};
