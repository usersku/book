package com.page.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;  

import com.page.book.domain.Book;
import com.page.pager.Expression;
import com.page.pager.PageBean;
import com.page.pager.PageConstants;

public class BookDao{
	private QueryRunner qr = new TxQueryRunner();

	/**
	 * �������ѯ
	 * 
	 */
	public PageBean<Book> findByCategory(String cid, int pc)
			throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid", "=", cid));
		return findByCriteria(exprList, pc);
	}

	/**
	 * ͨ�õĲ�ѯ����
	 * 
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	private PageBean<Book> findByCriteria(List<Expression> exprList, int pc)
			throws SQLException {
		/*
		 * 1. �õ�ps 2. �õ�tr 3. �õ�beanList 4. ����PageBean������
		 */
		/*
		 * 1. �õ�ps
		 */
		int ps = PageConstants.BOOK_PAGE_SIZE;// ÿҳ��¼��
		/*
		 * 2. ͨ��exprList������where�Ӿ�
		 */
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();// SQL�����ʺţ����Ƕ�Ӧ�ʺŵ�ֵ
		for (Expression expr : exprList) {
			/*
			 * ���һ�������ϣ� 1) ��and��ͷ 2) ���������� 3) �������������������=��!=��>��< ... is null��is
			 * nullû��ֵ 4) �����������is null����׷���ʺţ�Ȼ������params�����һ���ʺŶ�Ӧ��ֵ
			 */
			whereSql.append(" and ").append(expr.getName()).append(" ")
					.append(expr.getOperator()).append(" ");
			// where 1=1 and bid = ?
			if (!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}

		/*
		 * 3. �ܼ�¼��
		 */
		String sql = "select count(*) from t_book" + whereSql;
		Number number = (Number) qr.query(sql, new ScalarHandler(),
				params.toArray());
		int tr = number.intValue();// �õ����ܼ�¼��
		/*
		 * 4. �õ�beanList������ǰҳ��¼
		 */
		sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
		params.add((pc - 1) * ps);// ��ǰҳ���м�¼���±�
		params.add(ps);// һ����ѯ���У�����ÿҳ��¼��

		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(
				Book.class), params.toArray());

		/*
		 * 5. ����PageBean�����ò���
		 */
		PageBean<Book> pb = new PageBean<Book>();
		/*
		 * ����PageBeanû��url�����������Servlet���
		 */
		pb.setBeanList(beanList);
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);

		return pb;
	}
}
