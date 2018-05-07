package com.example.demo.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.nmap4j.Nmap4j;
import org.nmap4j.core.nmap.ExecutionResults;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.parser.OnePassParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/all")
public class DiscoveryModule {
	
	/**
	* Below method will discover the snmp and netconf devices and will store in a database
	* using hibernate
	* URL :- http://{localhost}:{port}/{ipAddressRange}
	* Example:-http://localhost:8083/10.132.32.192-255
	*/
	
	 private DiscoverRepository discoverRepository;

	    public DiscoveryModule(DiscoverRepository discoverRepository) {
	        this.discoverRepository = discoverRepository;
	    }
	
	
	@GetMapping(value="/insert/{ipaddress:.+}")
	public List<Discover> getNmaPDetails(HttpServletResponse response,@PathVariable("ipaddress") String paramName) throws IOException {
		
		String originalvalue = paramName.replaceAll("_","/");
		
		 Nmap4j nmap4j = new Nmap4j("/usr") ;
	   	 nmap4j.includeHosts(originalvalue) ;   	 
	   	 nmap4j.addFlags("--privileged --min-parallelism 1 -sT -sU -p 161,2022");

	   	  try{
	   	  nmap4j.execute() ; 
	   	 }catch(Exception e){
	   		 e.printStackTrace();
	   	  System.out.println("error execute");

	   	 }
	   	 if( !nmap4j.hasError() ) { 

		   		ExecutionResults res = nmap4j.getExecutionResults();
		   		String nmapRun = nmap4j.getOutput() ;
		   	   	
		   	   	OnePassParser opp = new OnePassParser() ;
		   	   	NMapRun nmapRun1 = opp.parse( nmapRun, OnePassParser.STRING_INPUT ) ;
		   	   	ArrayList<Host> hosts=nmapRun1.getHosts(); 
		   	   	
		   	   	   	
		   	   	for(Host ipAddr:hosts) {   	   		
		   	   		
		   	   //	try(Session session = HibernateUtil.getSessionFactory().openSession()){
					
		   	   		Discover obj = new Discover();
					//session.beginTransaction();			
									
					obj.setIp(ipAddr.getAddresses().get(0).getAddr());
					obj.setSnmp(ipAddr.getPorts().getPorts().get(2).getState().getState());
					obj.setNetconf(ipAddr.getPorts().getPorts().get(1).getState().getState());
					
					//session.saveOrUpdate(obj);											
					//session.getTransaction().commit();
					discoverRepository.save(obj);
					
					System.out.println("Successfully inserted.");
					
				/*}catch(HibernateException e){
					e.printStackTrace();
				}	*/	   			  		   	   	
		  	}
		   	   	
		   	  System.out.println(""+res.getOutput()+"\n");
		   	 }
	   	  else {
	   	   System.out.println( nmap4j.getExecutionResults().getErrors() ) ; 
	   	   }
		return discoverRepository.findAll();	   		
	}
	
	
	/*@GetMapping("/")
    public List<Discover> all() {


        return discoverRepository.findAll();
    }*/

	@RequestMapping(value="/{ipaddress:.+}",method=RequestMethod.GET)
	public void fetchingData(HttpServletResponse response,@PathVariable("ipaddress") String ipadd) throws IOException{
		
		System.out.println("entering fetchingData");
		PrintWriter out = response.getWriter();
		String ips="";
		String snmp="";
		String netconf="";
		String none="";
		String SnmpNetconf="";
		String ipaddress="";
		String systeminfo="";
		List<Discover> list = new ArrayList<Discover>();
				
		//try(Session session = HibernateUtil.getSessionFactory().openSession()){
	
			//if(session != null) {
			//session.beginTransaction();			
			//Criteria cr = session.createCriteria(Discover.class);
			//cr.add(Restrictions.eq("ip", ipadd));
			
			List<Discover> info = discoverRepository.findByIp(ipadd);
			
			
			for(Discover ls:info ){		
																				
				if(ls.getSnmp() != null){
					if(ls.getSnmp().contains("open")){
						snmp +="Snmp";
						ips = ls.getIp()+" = "+ snmp;
					}else if(ls.getSnmp().contains("closed")){
						none += "None";
						ips = ls.getIp()+" = "+  none;
					}
						
					
				}else if(ls.getNetconf() != null){
					if(ls.getNetconf().contains("open")){
						netconf +="Netconf";
						ips = ls.getIp()+" = "+  netconf;
					}else if(ls.getNetconf().contains("closed")){
						none += "None";
						ips = ls.getIp()+" = "+  none;
					}
				}else if(ls.getSnmp().contains("closed") || ls.getNetconf().contains("closed")){
					
					none += "None";
					ips = ls.getIp()+" = "+  none;
				}
				
				ipaddress = ls.getIp();
			}
			
			System.out.println("ipaddress and protocol "+ips);
			out.println(ips);
			
			
	}
		/*}catch(HibernateException e){
			e.printStackTrace();
		}*/
		
		//out.println(ips);
	


	@RequestMapping(value="/fetch",method=RequestMethod.GET)
	public void fetchingData(HttpServletResponse response) throws IOException {

		System.out.println("entering fetchingData");
		PrintWriter out = response.getWriter();

		//try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			
			//if(session != null) {

			//session.beginTransaction();
			//Criteria cr = session.createCriteria(Discover.class);

			List<Discover> lists = discoverRepository.findAll();

			String snmp = "Snmp";
			String netconf = "Netconf";
			String snmpnetconf = "";

			for (Discover rs : lists) {
				if (rs.getSnmp().equalsIgnoreCase(("open"))) {
					snmpnetconf = rs.getIp();
					out.println(snmpnetconf);
					System.out.println("Snmp" + snmpnetconf);
				} else if (rs.getSnmp().equalsIgnoreCase("open|filtered")) {
					snmpnetconf = rs.getIp();
					out.println(snmpnetconf);
					System.out.println("Snmp" + snmpnetconf);
				} else if (rs.getNetconf().contains("open")) {
					snmpnetconf = rs.getIp();
					out.println(snmpnetconf);
					System.out.println("Netconf" + snmpnetconf);
				} else if (rs.getNetconf().contains("open|filtered")) {
					snmpnetconf = rs.getIp();
					out.println(snmpnetconf);
					System.out.println("Netconf" + snmpnetconf);
				}

			}
		out.close();
		}
	}		


