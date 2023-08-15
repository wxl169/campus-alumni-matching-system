import { PLANET_LINK } from '@/constants';
import { DefaultFooter } from '@ant-design/pro-components';
const Footer: React.FC = () => {
  const defaultMessage = '重庆工程学院吴某出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'planet',
          title: '校园学友匹配系统',
          href: PLANET_LINK,
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
