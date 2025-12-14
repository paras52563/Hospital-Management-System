/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Patient;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class MedicalHistoryNotes extends JFrame {
    private final String patientId; 
    private final JPanel list = new JPanel();
    private JDateChooser dateChooser; 

    public MedicalHistoryNotes(String patientId){
        this.patientId = patientId;
        setTitle("Medical History — " + patientId); 
        setSize(1180,720);
        setLocationRelativeTo(null); 
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout()); 
        UIManager.put("Label.font", new Font("Segoe UI",Font.PLAIN,16));

        // ---------- HEADER ----------
        JPanel head = row(new Color(246,249,255), new EmptyBorder(26,30,22,30));
        JLabel t = new JLabel("🩺 Medical History",SwingConstants.CENTER);
        t.setFont(new Font("Segoe UI Semibold",Font.BOLD,30)); 
        t.setForeground(new Color(24,56,96));
        JLabel pid = new JLabel("Patient ID: "+patientId,SwingConstants.CENTER); 
        pid.setFont(new Font("Segoe UI",Font.PLAIN,16));
        pid.setForeground(new Color(100,100,100));
        head.setLayout(new BoxLayout(head,BoxLayout.Y_AXIS)); 
        t.setAlignmentX(0.5f); pid.setAlignmentX(0.5f);
        head.add(t); head.add(Box.createVerticalStrut(6)); head.add(pid);

        // ---------- DATE FILTER ----------
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,6));
        filterPanel.setBackground(new Color(246,249,255));

        JLabel filterLabel = new JLabel("📅 Filter by Date: ");
        filterLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setPreferredSize(new Dimension(180,30));

        JButton filterBtn = bigBtn("🔍 Apply Filter", new Color(40,167,69));
        JButton resetBtn = bigBtn("⟳ Reset", new Color(23,162,184));

        filterPanel.add(filterLabel);
        filterPanel.add(dateChooser);
        filterPanel.add(filterBtn);
        filterPanel.add(resetBtn);

        head.add(Box.createVerticalStrut(12));
        head.add(filterPanel);

        add(head,BorderLayout.NORTH);

        // ---------- LIST ----------
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS)); 
        list.setBackground(new Color(246,249,255));
        JScrollPane sp = new JScrollPane(list); 
        sp.setBorder(new EmptyBorder(20,80,20,80));
        sp.getVerticalScrollBar().setUnitIncrement(20); 
        add(sp,BorderLayout.CENTER);

        // ---------- FOOTER ----------
        JButton back = bigBtn("🔙 Back to Dashboard", new Color(220,53,69));
        back.addActionListener(e->{ dispose(); new PatientDashboard(patientId); });
        JPanel foot = row(new Color(246,249,255), new EmptyBorder(16,60,16,60));
        foot.add(back); add(foot,BorderLayout.SOUTH);

        // Load all initially
        load(null);

        // Filter button
        filterBtn.addActionListener(e -> {
            if(dateChooser.getDate() == null){
                JOptionPane.showMessageDialog(this,"Please select a date.","Invalid Date",JOptionPane.WARNING_MESSAGE);
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = sdf.format(dateChooser.getDate());
                load(selectedDate);
            }
        });

        // Reset filter
        resetBtn.addActionListener(e -> {
            dateChooser.setDate(null);
            load(null);
        });

        setVisible(true);
    }

    // ---------- LOAD RECORDS ----------
    private void load(String filterDate){
        list.removeAll();
        try(Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital_db","root","")){
            String sql = "SELECT record_id,doctor_id,diagnosis,treatment,notes,record_date " +
                         "FROM medical_records WHERE patient_id=? ";
            if(filterDate != null) sql += "AND record_date = ? ";
            sql += "ORDER BY record_date DESC";
            
            try(PreparedStatement ps = c.prepareStatement(sql)){
                ps.setString(1,patientId);
                if(filterDate != null) ps.setString(2,filterDate);
                
                ResultSet rs = ps.executeQuery();
                int n=0;
                while(rs.next()){
                    n++;
                    String doc = doctor(c, rs.getString("doctor_id"));
                    list.add(card(rs.getInt("record_id"), rs.getDate("record_date")+"", doc,
                                  rs.getString("diagnosis"), rs.getString("treatment"), rs.getString("notes")));
                    list.add(Box.createVerticalStrut(20));
                }
                if(n==0){ 
                    JLabel none=new JLabel("⚠️ No medical records found."); 
                    none.setFont(new Font("Segoe UI",Font.ITALIC,16));
                    none.setForeground(new Color(120,120,120));
                    none.setAlignmentX(0.5f); 
                    list.add(none); 
                }
            }
        }catch(Exception ex){ ex.printStackTrace(); JOptionPane.showMessageDialog(this,"DB Error: "+ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE); }
        list.revalidate();
        list.repaint();
    }

    private String doctor(Connection c,String id){
        try(PreparedStatement ps=c.prepareStatement("SELECT name,speciality FROM doctor WHERE doctor_id=?")){
            ps.setString(1,id); ResultSet r=ps.executeQuery(); 
            if(r.next()) return r.getString("name")+" ("+r.getString("speciality")+")";
        }catch(Exception ignored){}
        return "Doctor ID: "+id;
    }

    // ---------- UI Helpers ----------
    private JPanel card(int recId,String date,String doctor,String diag,String treat,String notes){
        JPanel p = new JPanel(new BorderLayout(12,12));
        p.setBackground(Color.WHITE);
        p.setBorder(boxBorder(new Color(210,214,222),1,18,22,24));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel h = new JLabel("📌 Record #"+recId+"   •   🗓 "+date);
        h.setFont(new Font("Segoe UI Semibold",Font.BOLD,18)); 
        h.setForeground(new Color(25,118,210));
        p.add(h,BorderLayout.NORTH);

        JPanel body = new JPanel(); body.setBackground(Color.WHITE);
        body.setLayout(new GridLayout(0,1,8,8));
        body.add(field("👨‍⚕️ Doctor", doctor, new Color(33,150,243)));
        body.add(field("🩺 Diagnosis", diag, new Color(233,30,99)));
        body.add(field("💊 Treatment", treat, new Color(0,200,83)));
        body.add(field("📝 Notes", notes, new Color(255,152,0)));
        p.add(body,BorderLayout.CENTER);

        // Hover effect
        p.addMouseListener(new MouseAdapter(){ 
            public void mouseEntered(MouseEvent e){
                p.setBorder(boxBorder(new Color(25,118,210),2,18,22,24)); 
                p.setBackground(new Color(248,251,255)); 
            }
            public void mouseExited(MouseEvent e){
                p.setBorder(boxBorder(new Color(210,214,222),1,18,22,24)); 
                p.setBackground(Color.WHITE); 
            }
        });
        return p;
    }

    private JPanel field(String label,String value,Color accent){
        JPanel row = new JPanel(new BorderLayout(10,0)); 
        row.setBackground(Color.WHITE);
        JLabel l = new JLabel(label+": "); 
        l.setFont(new Font("Segoe UI Semibold",Font.BOLD,15)); 
        l.setForeground(accent);
        JLabel v = new JLabel(html(value)); 
        v.setFont(new Font("Segoe UI",Font.PLAIN,15));
        v.setForeground(new Color(45,45,45));
        row.add(l,BorderLayout.WEST); row.add(v,BorderLayout.CENTER); 
        return row;
    }

    private static String html(String s){
        if(s==null||s.isBlank()) return "—";
        return "<html><body style='width:780px; font-size:14px;'>" + escape(s) + "</body></html>";
    }
    private static String escape(String s){ return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }

    private JPanel row(Color bg, Border b){ JPanel p=new JPanel(new FlowLayout(FlowLayout.RIGHT)); p.setBackground(bg); p.setBorder(b); return p; }

    private Border boxBorder(Color line,int lw,int radius,int padV,int padH){
        return new CompoundBorder(new LineBorder(line,lw,true), new EmptyBorder(padV,padH,padV,padH));
    }

    private JButton bigBtn(String text, Color bg){
        JButton b = new JButton(text); 
        b.setFont(new Font("Segoe UI Semibold",Font.BOLD,15));
        b.setForeground(Color.WHITE); 
        b.setBackground(bg);
        b.setFocusPainted(false); 
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new RoundedBorder(16)); 
        b.setPreferredSize(new Dimension(240,46)); 
        return b;
    }

    static class RoundedBorder extends AbstractBorder{
        private final int r; RoundedBorder(int r){this.r=r;}
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h){
            g.setColor(new Color(190,190,190)); g.drawRoundRect(x,y,w-1,h-1,r,r);
        }
    }

    public static void main(String[] args){ 
        SwingUtilities.invokeLater(()-> new MedicalHistoryNotes("PAT28102")); 
    }
}






