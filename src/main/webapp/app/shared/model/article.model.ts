import { Moment } from 'moment';
import { IComment } from 'app/shared/model/comment.model';
import { IUser } from 'app/shared/model/user.model';
import { Category } from 'app/shared/model/enumerations/category.model';

export interface IArticle {
  id?: number;
  category?: Category;
  posted?: string;
  title?: string;
  tag?: string;
  summary?: string;
  photoURL?: string;
  photoAuthor?: string;
  poweredBy?: string;
  poweredByURL?: string;
  content?: string;
  hero?: boolean;
  comments?: IComment[];
  author?: IUser;
}

export const defaultValue: Readonly<IArticle> = {
  hero: false,
};
