package com.customer.esweetcustomer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.customer.esweetcustomer.Model.Customer;
import com.customer.esweetcustomer.Model.Invoice;
import com.customer.esweetcustomer.Model.InvoiceItem;
import com.customer.esweetcustomer.Model.InvoicePdf;
import com.customer.esweetcustomer.Model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ir.androidexception.datatable.DataTable;
import ir.androidexception.datatable.model.DataTableHeader;
import ir.androidexception.datatable.model.DataTableRow;

public class InvoiceActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public CollectionReference orderCollection;
    public CollectionReference orderItemCollection;
    public CollectionReference userCollection;
    public CollectionReference addCollection;
    private Button printBtn;
    DataTable dataTable;
    DataTableHeader header;
    private ArrayList<DataTableRow> rows = new ArrayList<>();
    TextView cusName,houseNo,country,sub,delivery,tot,invoicId,datetime;
    private double totl;
    ArrayList<InvoicePdf> pdf = new ArrayList<>();
    private int y = 0;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        orderCollection = db.collection("Invoice");
        orderItemCollection = db.collection("InvoiceItem");
        userCollection = db.collection("Customers");
//        addCollection = db.collection("Address");

        Bundle bundle = getIntent().getExtras();
        String invoiceId = bundle.getString("invoiceId");
        dataTable = findViewById(R.id.data_table);
Log.d("invoiceIdinvoiceIdinvoiceId>>>>",""+invoiceId);

        loading = new ProgressDialog(this);

        printBtn = findViewById(R.id.oldPrintBtn);
        cusName = findViewById(R.id.customer_name);
        houseNo = findViewById(R.id.customer_addresNo);
//        city = findViewById(R.id.customer_addres_city);
        country = findViewById(R.id.customer_country);
        sub = findViewById(R.id.invoice_sub);
        delivery = findViewById(R.id.invoice_delivery);
        tot = findViewById(R.id.invoice_tot);
        invoicId = findViewById(R.id.invoice_docid);
        datetime = findViewById(R.id.datetimeL);


        setAddress(invoiceId);

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPdf(invoiceId);
            }
        });

    }

    private void printPdf(String invoiceId) {
        PdfDocument myPdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();
        forLinePaint.setColor(Color.rgb(0,50,250));
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
        PdfDocument.Page myPage = myPdfDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));

        canvas.drawText("Cake Town",20,20,paint);
        paint.setTextSize(8.5f);
        canvas.drawText(houseNo.getText().toString(),20,40,paint);
//        canvas.drawText(city.getText().toString(),20,55,paint);
        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        forLinePaint.setStrokeWidth(2);
        canvas.drawLine(20,65,230,65,forLinePaint);

        canvas.drawText("Customer Name: "+cusName.getText().toString(),20,80,paint);
        canvas.drawLine(20,90,230,90,forLinePaint);

        y = 115;
        for (InvoicePdf invoicePdf : pdf){
            canvas.drawText(invoicePdf.getProduct(),20,y,paint);
            canvas.drawText(invoicePdf.getQty(),120,y,paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(invoicePdf.getAmount(),230,y,paint);
            paint.setTextAlign(Paint.Align.LEFT);
            y = y+20;
        }

        canvas.drawLine(20,y,230,y,forLinePaint);
        paint.setTextSize(10f);
        y = y+15;
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(sub.getText().toString(),230,y,paint);

        y = y+15;
        canvas.drawText(delivery.getText().toString(),230,y,paint);

        y = y+15;
        canvas.drawText(tot.getText().toString(),230,y,paint);
        paint.setTextAlign(Paint.Align.LEFT);

        paint.setTextSize(8.5f);
        y = y+35;
        canvas.drawText("Date: "+datetime.getText().toString(),20,y,paint);
        y = y+15;
        canvas.drawText(invoicId.getText().toString(),20,y,paint);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(12f);

        y = y+30;
        canvas.drawText("Thank you!",canvas.getWidth()/2,y,paint);
        myPdfDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir("/"),invoiceId+".pdf");

        try {
            myPdfDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPdfDocument.close();

        Toast.makeText(this, "Invoice Saved Successfully", Toast.LENGTH_SHORT).show();

    }

    private void setAddress(String invoiceId) {
        orderCollection.document(invoiceId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Invoice invoice = documentSnapshot.toObject(Invoice.class);
                userCollection.document(invoice.getCustomerDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Customer customer = documentSnapshot.toObject(Customer.class);
                        cusName.setText(customer.getName());
                        houseNo.setText(customer.getAddress());

                        country.setText("Sri Lanka");
                        invoicId.setText("Invoice Id :"+invoiceId);
                        datetime.setText(invoice.getDate()+"/"+invoice.getTime());

                        orderItemCollection.whereEqualTo("invoiceDocId",invoiceId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                header = new DataTableHeader.Builder()
                                        .item("ProductId",5)
                                        .item("Product Name",5)
                                        .item("Price",5)
                                        .item("Qty",5)
                                        .item("Amount",5)
                                        .build();

                                double del = 59.53;
                                totl = 00.00;


                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    InvoiceItem item = document.toObject(InvoiceItem.class);

                                    double qt = Double.parseDouble(item.getItemQty());

                                    db.collection("Products").document(item.getProductDocId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Product product = documentSnapshot.toObject(Product.class);

                                            double price = Double.parseDouble(String.valueOf(product.getProductPrice()));
                                            totl =+ totl + (price * qt);

                                            sub.setText("Sub Total :Rs."+String.valueOf(totl));
                                            delivery.setText("Delivery :Rs."+String.valueOf(del));
                                            tot.setText("Total :Rs."+String.valueOf(totl + del));

                                            InvoicePdf pdfD = new InvoicePdf();
                                            pdfD.setProduct(product.getProductName());
                                            pdfD.setQty(item.getItemQty());
                                            pdfD.setAmount(String.valueOf(Double.parseDouble(item.getItemQty()) * Double.parseDouble(String.valueOf(product.getProductPrice()))));
                                            pdf.add(pdfD);

                                            Log.d("aiyooo",product.getProductName());
                                            DataTableRow row = new DataTableRow.Builder()
                                                    .value(item.getProductDocId())
                                                    .value(product.getProductName())
                                                    .value("Rs:"+product.getProductPrice())
                                                    .value(item.getItemQty())
                                                    .value("Rs:"+String.valueOf(Double.parseDouble(item.getItemQty()) * Double.parseDouble(String.valueOf(product.getProductPrice()))))
                                                    .build();
                                            rows.add(row);
                                            dataTable.setHeader(header);
                                            dataTable.setRows(rows);
                                            dataTable.inflate(InvoiceActivity.this);
                                        }
                                    });
                                }

                            }
                        });
                    }
                });
        }
    });




}
}